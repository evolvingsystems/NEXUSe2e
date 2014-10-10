/**
 *  NEXUSe2e Business Messaging Open Source
 *  Copyright 2000-2009, Tamgroup and X-ioma GmbH
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation version 2.1 of
 *  the License.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.nexuse2e.ui.controller.certificate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PasswordFinder;
import org.bouncycastle.util.encoders.Hex;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.CertificateType;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ReferencedCertificateException;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.PartnerPojo;
import org.nexuse2e.ui.form.CertificatePromotionForm;
import org.nexuse2e.ui.form.CertificatePropertiesForm;
import org.nexuse2e.ui.form.CertificateRequestForm;
import org.nexuse2e.ui.form.ProtectedFileAccessForm;
import org.nexuse2e.util.CertificateUtil;
import org.nexuse2e.util.EncryptionUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for certificate maintenance
 *
 * @author Jonas Reese
 */
@Controller
public class CertificateController {

    protected static Logger LOG = Logger.getLogger(CertificateController.class);

    
    @RequestMapping("/RequestOverview.do")
    public String requestOverview(Model model, EngineConfiguration engineConfiguration) throws NexusException {

        CertificatePojo certificateRequest = engineConfiguration.getFirstCertificateByType(CertificateType.REQUEST.getOrdinal(), false);
        CertificatePojo certificateKey = null;
        if (certificateRequest == null) {
            LOG.error("no certificate request found in database");
        } else {
            certificateKey = engineConfiguration.getFirstCertificateByType(CertificateType.PRIVATE_KEY.getOrdinal(), true);
        }
        model.addAttribute("createRequest", true);
        model.addAttribute("importBackup", true);
        model.addAttribute("showRequest", false);
        model.addAttribute("exportRequest", false);
        model.addAttribute("exportPKCS12", false);
        model.addAttribute("deleteRequest", true);
        if (certificateRequest != null && certificateKey != null) {
            CertificateUtil.getPKCS10Request(certificateRequest);
            model.addAttribute("createRequest", false);
            model.addAttribute("importBackup", false);
            model.addAttribute("showRequest", true);
            model.addAttribute("exportRequest", true);
            model.addAttribute("exportPKCS12", true);
        }
        if (certificateRequest == null && certificateKey == null) {
            model.addAttribute("deleteRequest", false);
        }


        return "pages/certificates/request_overview";
    }
    
    @RequestMapping("/RequestImportCert.do")
    public String requestImportCert(ProtectedFileAccessForm form, EngineConfiguration engineConfiguration) throws NexusException {

        return "pages/certificates/request_import_cert";
    }

    @RequestMapping("/RequestVerifyCertChainCert.do")
    public String requestVerifyCertChainCert(
            Model model,
            @Valid ProtectedFileAccessForm form,
            BindingResult bindingResult,
            EngineConfiguration engineConfiguration) throws NexusException, FileNotFoundException, IOException {

        if (bindingResult.hasErrors()) {
            return requestImportCert(form, engineConfiguration);
        }
        
        ByteArrayInputStream bais = new ByteArrayInputStream(form.getCertificate().getBytes());
        BigInteger requestModulus;
        BigInteger requestExponent;
        List<X509Certificate> certs;
        try {
            ZipInputStream zip = new ZipInputStream(bais);

            List<CertificatePojo> requestPojos = engineConfiguration.getCertificates(CertificateType.REQUEST.getOrdinal(), null);
            if (requestPojos == null || requestPojos.size() == 0) {
                LOG.warn("No request found in configuration");
                bindingResult.reject("certificates.import.norequest", "No CSR found");
                return requestImportCert(form, engineConfiguration);
            }
            
            if (requestPojos.size() > 1) {
                LOG.warn("there is more than one request in database, using first one!");
            }
            CertificatePojo requestPojo = requestPojos.get(0);

            PKCS10CertificationRequest pkcs10req = CertificateUtil.getPKCS10Request(requestPojo);

            RSAPublicKey pub = (RSAPublicKey) pkcs10req.getPublicKey();

            requestModulus = pub.getModulus();
            requestExponent = pub.getPublicExponent();

            certs = new ArrayList<X509Certificate>();

            while (zip.getNextEntry() != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                byte[] buf = new byte[1024];
                int len;
                while ((len = zip.read(buf)) > 0) {
                    baos.write(buf, 0, len);
                }

                byte[] data = baos.toByteArray();
                zip.closeEntry();

                X509Certificate cert = CertificateUtil.getX509Certificate(data);
                if (cert != null) {
                    certs.add(cert);
                }
            }
        } catch (Exception e) {
            LOG.debug(e);
            String error = StringUtils.isBlank(e.getMessage()) ? e.getClass().getSimpleName() : e.getMessage();
            bindingResult.reject("certificates.import.error", new Object[]{ error }, "Error: " + error);
            return requestImportCert(form, engineConfiguration);
        }
        if (certs.size() == 0) { // no zipfile?
            X509Certificate cert = CertificateUtil.getX509Certificate(form.getCertificate().getBytes());
            if (cert != null) {
                certs.add(cert);
            } else {
                bindingResult.reject("certificates.import.invalidfile", "Invalid file");
                return requestImportCert(form, engineConfiguration);
            }
        }

        if (certs.size() > 0) {
            List<CertificatePojo> cacertPojo = engineConfiguration.getCertificates(CertificateType.CA.getOrdinal(), null);

            List<X509Certificate> cacerts = new ArrayList<X509Certificate>();

            for (CertificatePojo pojo : cacertPojo) {
                X509Certificate caCert = CertificateUtil.getX509Certificate(pojo);
                cacerts.add(caCert);
            }

            X509Certificate headcert = null;
            for (X509Certificate cert : certs) {

                BigInteger modulus = ((RSAPublicKey) cert.getPublicKey()).getModulus();
                BigInteger exponent = ((RSAPublicKey) cert.getPublicKey()).getPublicExponent();

                if (modulus.equals(requestModulus) && exponent.equals(requestExponent)) {
                    headcert = cert;
                }
            }

            if (headcert == null) {
                LOG.error("No matching headcertificate found for request!");
                bindingResult.reject("certificates.import.noheadcertinrequest", "No matching headcertificate found for request");
                return requestImportCert(form, engineConfiguration);
            }

            Vector<CertificatePropertiesForm> caImports = new Vector<CertificatePropertiesForm>();
            PKIXCertPathBuilderResult result = CertificateUtil.getCertificateChain(headcert, certs, cacerts);

            if (null == result) {
                // null indicates that the chain could not be constructed in it's entirety, user has to supply CA certificates.
                return "pages/certificates/ca_certificate_missing";
            }

            X509Certificate root = result.getTrustAnchor().getTrustedCert();
            CertificatePropertiesForm rootCertForm = new CertificatePropertiesForm();
            rootCertForm.setCertificateProperties(root);
            rootCertForm.setAlias(CertificateUtil.createCertificateId(root));

            boolean found = false;
            try {
                for (X509Certificate cacert : cacerts) {
                    if (CertificateUtil.getMD5Fingerprint(cacert).equals(CertificateUtil.getMD5Fingerprint(root))) {
                        found = true;
                    }
                }
            } catch (CertificateEncodingException e) {
                String error = StringUtils.isBlank(e.getMessage()) ? e.getClass().getSimpleName() : e.getMessage();
                bindingResult.reject("certificates.import.error", new Object[]{ error }, "Error: " + error);
                return requestImportCert(form, engineConfiguration);
            }
            if (!found) {
                caImports.addElement(rootCertForm);
            }

            List<CertificatePropertiesForm> chainCerts = new ArrayList<CertificatePropertiesForm>();
            for (Certificate cert : result.getCertPath().getCertificates()) {
                X509Certificate chaincert = (X509Certificate) cert;

                CertificatePropertiesForm chainForm = new CertificatePropertiesForm();
                chainForm.setCertificateProperties(chaincert);
                chainForm.setAlias(CertificateUtil.createCertificateId(chaincert));
                chainCerts.add(chainForm);
            }
            chainCerts.add(rootCertForm);

            model.addAttribute("cacertsimports", caImports);
            model.addAttribute("chain", chainCerts);
            model.addAttribute("cacertsimports", caImports);
            model.addAttribute("chain", chainCerts);
        }

        return "pages/certificates/request_import_cert";
    }

    @RequestMapping("/RequestExportCSR.do")
    public String requestExportCsr(
            Model model,
            ProtectedFileAccessForm protectedFileAccessForm,
            BindingResult bindingResult,
            EngineConfiguration engineConfiguration) throws NexusException, FileNotFoundException, IOException {

        List<CertificatePojo> certificates = engineConfiguration.getCertificates(CertificateType.REQUEST.getOrdinal(), null);
        if (certificates == null || certificates.isEmpty()) {
            return requestOverview(model, engineConfiguration);
        }

        PKCS10CertificationRequest certRequest = CertificateUtil.getPKCS10Request(certificates.get(0));
        String subject = certRequest.getCertificationRequestInfo().getSubject().toString();
        
        CertificateRequestForm certRequestForm = new CertificateRequestForm();
        certRequestForm.setRequestProperties(subject);
        model.addAttribute("request", certRequestForm);
        protectedFileAccessForm.setFormat(2);
        protectedFileAccessForm.setStatus(2);
        
        return "pages/certificates/request_export_csr";
    }
    
    @RequestMapping("/RequestSaveCSRFile.do")
    public String requestSaveCsrFile(
            Model model,
            ProtectedFileAccessForm form,
            EngineConfiguration engineConfiguration) throws IOException, CertificateEncodingException, NexusException {
        
        if (form.getStatus() == 1) {
            String path = form.getCertificatePath();
            File certFile = new File(path);
            FileOutputStream fos = new FileOutputStream(certFile);

            CertificatePojo certificateRequest = engineConfiguration.getFirstCertificateByType(CertificateType.REQUEST.getOrdinal(), true);
            PKCS10CertificationRequest csr = CertificateUtil.getPKCS10Request(certificateRequest);
            byte[] data = new byte[0];
            if (form.getFormat() == ProtectedFileAccessForm.PEM) {
                String pemCSR = CertificateUtil.getPemData(csr);
                data = pemCSR.getBytes();
            } else {
                data = (byte[]) CertificateUtil.getDERData(csr);
            }

            fos.write(data);
            fos.flush();
            fos.close();
        } else {
            model.addAttribute("type", "csr");
            CertificatePojo certificateRequest = engineConfiguration.getFirstCertificateByType(CertificateType.REQUEST.getOrdinal(), true);

            model.addAttribute("nxCertificateId", "" + certificateRequest.getNxCertificateId());
            if (form.getFormat() == ProtectedFileAccessForm.PEM) {
                model.addAttribute("format", "pem");
            } else {
                model.addAttribute("format", "der");
            }
        }

        return requestOverview(model, engineConfiguration);
    }

    @RequestMapping("/RequestExportPKCS12.do")
    public String requestExportPkcs12(
            Model model,
            ProtectedFileAccessForm form,
            EngineConfiguration engineConfiguration) {

        form.setStatus(2);
        return "pages/certificates/request_export_pkcs12";
    }

    @RequestMapping("/RequestSavePKCS12File.do")
    public String requestSavePKCS12File(
            Model model,
            ProtectedFileAccessForm form,
            EngineConfiguration engineConfiguration)
                    throws IOException, CertificateEncodingException, NexusException {

        if (form.getStatus() == 1) {
            LOG.debug("path:" + form.getCertificatePath());

            CertificatePojo requestPojo = engineConfiguration.getFirstCertificateByType(CertificateType.REQUEST.getOrdinal(), true);
            if (requestPojo == null) {
                LOG.error("no request found in database");
                return "pages/certificates/request_export_pkcs12";
            }
            CertificatePojo privKeyPojo = engineConfiguration.getFirstCertificateByType(CertificateType.PRIVATE_KEY.getOrdinal(), true);
            if (privKeyPojo == null) {
                LOG.error("no request found in database");
                return "pages/certificates/request_export_pkcs12";
            }
            StringBuffer sb = new StringBuffer();

            KeyPair keyPair = CertificateUtil.getKeyPair(privKeyPojo);
            PKCS10CertificationRequest pkcs10Request = CertificateUtil.getPKCS10Request(requestPojo);
            sb.append(CertificateUtil.getPemData(pkcs10Request));
            sb.append("\n");
            sb.append(CertificateUtil.getPemData(keyPair, EncryptionUtil.decryptString(privKeyPojo.getPassword())));

            File certFile = new File(form.getCertificatePath());
            FileOutputStream fos = new FileOutputStream(certFile);
            fos.write(sb.toString().getBytes());
            fos.flush();
            fos.close();
        } else {
            model.addAttribute("type", "privatepem");
        }

        return requestOverview(model, engineConfiguration);
    
    }
    
    @RequestMapping("/RequestCreate.do")
    public String requestCreate(
            Model model,
            CertificateRequestForm form,
            EngineConfiguration engineConfiguration)
                    throws NexusException {
        
        List<CertificatePojo> list = engineConfiguration.getCertificates(CertificateType.PRIVATE_KEY.getOrdinal(), null);
        if (list != null && list.size() > 0) {
            return requestOverview(model, engineConfiguration);
        }
        list = engineConfiguration.getCertificates(CertificateType.REQUEST.getOrdinal(), null);
        if (list != null && list.size() > 0) {
            return requestOverview(model, engineConfiguration);
        }

        return "pages/certificates/request_create";
    }

    @RequestMapping("/RequestSaveRequest.do")
    public String requestSaveRequest(
            Model model,
            @Valid CertificateRequestForm form,
            BindingResult bindingResult,
            EngineConfiguration engineConfiguration)
                    throws NexusException,
                    NoSuchAlgorithmException,
                    NoSuchProviderException,
                    CertificateEncodingException,
                    IOException {

        if (bindingResult.hasErrors()) {
            return requestCreate(model, form, engineConfiguration);
        }

        String cn = form.getCommonName();
        String o = form.getOrganisation();
        String ou = form.getOrganisationUnit();
        String l = form.getLocation();
        String s = form.getState();
        String c = form.getCountryCode();
        String e = form.getEmail();
        String pwd = form.getPassword();

        KeyPair keyPair = CertificateUtil.generateKeyPair(form.getKeyLength());

        PKCS10CertificationRequest pkcs10Request = CertificateUtil.generatePKCS10CertificateRequest(keyPair, cn, o, ou, l, c, s, e);

        // Request
        CertificatePojo certificate = CertificateUtil.createPojoFromPKCS10(pkcs10Request);
        CertificatePojo privateKeyPojo = CertificateUtil.createPojoFromKeyPair(keyPair, certificate.getName(), pwd);
        
        List<CertificatePojo> certs = new ArrayList<CertificatePojo>();
        certs.add(certificate);
        certs.add(privateKeyPojo);
        
        File certbackup = new File(Engine.getInstance().getNexusE2ERoot(), "WEB-INF");
        certbackup = new File(certbackup, "backup");
        if (!certbackup.exists()) {
            certbackup.mkdirs();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
        String date = sdf.format(new Date());
        
        File currentDir = new File(certbackup,"requestCreate_" + date);
        if (!currentDir.exists()) {
            currentDir.mkdirs();
        }
        FileUtils.writeByteArrayToFile(new File(currentDir,"privKey.pem"), privateKeyPojo.getBinaryData());
        FileUtils.write(new File(currentDir,"request.pem"), CertificateUtil.getPemData(pkcs10Request));

        engineConfiguration.updateCertificates(certs);

        return requestOverview(model, engineConfiguration);
    }
    
    @RequestMapping("/RequestShowCSR.do")
    public String requestShowCsr(
            Model model, CertificateRequestForm form, EngineConfiguration engineConfiguration)
                    throws NexusException, CertificateEncodingException {

        CertificatePojo certificate = engineConfiguration.getFirstCertificateByType(CertificateType.REQUEST.getOrdinal(), true);
        PKCS10CertificationRequest certRequest = CertificateUtil.getPKCS10Request(certificate);
        String pemCSR = CertificateUtil.getPemData(certRequest);
        if (certRequest == null) {
            return requestOverview(model, engineConfiguration);
        }
        String subject = certRequest.getCertificationRequestInfo().getSubject().toString();
        form.setSubject(subject);
        form.setPemCSR(pemCSR);
        form.setRequestProperties(subject);

        return "pages/certificates/request_show_csr";
    }
    
    @RequestMapping("/RequestImportBackup.do")
    public String requestImportBackup(
            ProtectedFileAccessForm form, Model model, EngineConfiguration engineConfiguration)
                    throws NexusException {
        
        return "pages/certificates/request_import_backup";
    }
    
    
    private CertificatePojo createPojo(Object o, String password) {
        if (o == null) {
            return null;
        }

        if (o instanceof KeyPair) {
            CertificatePojo key = CertificateUtil.createPojoFromKeyPair((KeyPair) o, "import", password);
            if (key == null) {
                LOG.error("unable to create certificate pojo from keypair!");
                return null;
            }
            return key;
        } else if (o instanceof PKCS10CertificationRequest) {
            CertificatePojo request = CertificateUtil.createPojoFromPKCS10((PKCS10CertificationRequest) o);
            if (request == null) {
                LOG.error("unable to create certificate pojo from pkcs10 request!");
                return null;
            }
            return request;
        }

        return null;
    }
    
    @RequestMapping("/RequestSaveBackup.do")
    public String requestSaveBackup(
            @Valid ProtectedFileAccessForm form,
            BindingResult bindingResult,
            Model model,
            EngineConfiguration engineConfiguration) throws NexusException, IOException {
        
        if (bindingResult.hasErrors()) {
            return requestImportBackup(form, model, engineConfiguration);
        }
        
        final String pwd = form.getPassword() == null ? "" : form.getPassword();
        final byte[] data = form.getCertificate().getBytes();

        StringReader sr = new StringReader(new String(data));

        PasswordFinder passwordFinder = new PasswordFinder() {
            public char[] getPassword() {
                return pwd.toCharArray();
            }
        };
        
        try (PEMReader reader = new PEMReader(sr, passwordFinder)) {
    
            Object o = null;
            try {
                o = reader.readObject();
            } catch (IOException ioex) {
                LOG.debug(ioex);
            }
            CertificatePojo pojo1 = createPojo(o, pwd);
            if (pojo1 == null) {
                bindingResult.rejectValue("certificate", "request.import.backup.invalid", "Invalid input file");
                return requestImportBackup(form, model, engineConfiguration);
            }
            o = null;
            try {
                o = reader.readObject();
            } catch (IOException ioex) {
                LOG.debug(ioex);
            }
            CertificatePojo pojo2 = createPojo(o, pwd);
            if (pojo2 == null) {
                bindingResult.rejectValue("certificate", "request.import.backup.invalid", "Invalid input file");
                return requestImportBackup(form, model, engineConfiguration);
            }

            if ((pojo1.getType() == CertificateType.PRIVATE_KEY.getOrdinal() && pojo2.getType() == CertificateType.REQUEST.getOrdinal())
                    || (pojo2.getType() == CertificateType.PRIVATE_KEY.getOrdinal() && pojo1.getType() == CertificateType.REQUEST.getOrdinal())) {
                List<CertificatePojo> pojos = engineConfiguration.getCertificates(CertificateType.PRIVATE_KEY.getOrdinal(), null);
                if (pojos != null && pojos.size() > 0) {
                    bindingResult.reject(null, "There are already one or more private keys in database.");
                    return requestImportBackup(form, model, engineConfiguration);
                }
                pojos = engineConfiguration.getCertificates(CertificateType.REQUEST.getOrdinal(), null);
                if (pojos != null && pojos.size() > 0) {
                    bindingResult.reject(null, "There are already one or more private keys in database.");
                    return requestImportBackup(form, model, engineConfiguration);
                }
    
                pojos = new ArrayList<CertificatePojo>();
                pojos.add(pojo1);
                pojos.add(pojo2);
    
                engineConfiguration.updateCertificates(pojos);
    
            } else {
                bindingResult.reject(null, "no valid request and private key object found!");
                return requestImportBackup(form, model, engineConfiguration);
            }
        }

        return requestOverview(model, engineConfiguration);
    }
    
    @RequestMapping("/RequestDelete.do")
    public String requestDelete(Model model, EngineConfiguration engineConfiguration) throws NexusException {

        List<CertificatePojo> requests = engineConfiguration.getCertificates(CertificateType.REQUEST.getOrdinal(), null);
        if (requests.size() > 1) {
            LOG.warn("there is more than one request in database!");
        }
        List<CertificatePojo> privateKeys = engineConfiguration.getCertificates(CertificateType.PRIVATE_KEY.getOrdinal(), null);

        requests.addAll(privateKeys);
        engineConfiguration.deleteCertificates(requests);
        
        return requestOverview(model, engineConfiguration);
    }
    
    @RequestMapping("/StagingList.do")
    public String stagingList(Model model, EngineConfiguration engineConfiguration)
            throws NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException, NoSuchProviderException, NexusException {

        List<CertificatePropertiesForm> certs = new ArrayList<CertificatePropertiesForm>();
        List<CertificatePojo> certPojos = engineConfiguration.getCertificates(CertificateType.STAGING.getOrdinal(), Constants.CERTIFICATECOMPARATOR);
        if (certPojos != null) {
            for (CertificatePojo certificate : certPojos) {
                byte[] data = certificate.getBinaryData();
                if (data != null) {

                    KeyStore jks = KeyStore.getInstance(CertificateUtil.DEFAULT_KEY_STORE, CertificateUtil.DEFAULT_JCE_PROVIDER);
                    jks.load(new ByteArrayInputStream(data), EncryptionUtil.decryptString(certificate.getPassword()).toCharArray());

                    Enumeration<String> aliases = jks.aliases();
                    if (!aliases.hasMoreElements()) {
                        LOG.info("No certificate aliases found!");
                        continue;
                    }

                    while (aliases.hasMoreElements()) {
                        String alias = aliases.nextElement();
                        if (jks.isKeyEntry(alias)) {
                            Certificate[] certsArray = jks.getCertificateChain(alias);

                            if ((certsArray != null) && (certsArray.length != 0)) {
                                CertificatePropertiesForm form = new CertificatePropertiesForm();
                                form.setCertificateProperties((X509Certificate) certsArray[0]);
                                form.setNxCertificateId(certificate.getNxCertificateId());
                                Date date = certificate.getCreatedDate();
                                SimpleDateFormat databaseDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                form.setCreated(databaseDateFormat.format(date));
                                String issuerCN = CertificateUtil.getIssuer((X509Certificate) certsArray[certsArray.length - 1], X509Name.CN);
                                form.setIssuer(issuerCN);
                                certs.add(form);

                                break;
                            }
                        }
                    } // while
                } else {
                    LOG.error("Certificate entry does not contain any binary data: " + certificate.getName());
                }
            } // while
        } else {
            LOG.info("no certs found");
        }

        model.addAttribute("collection", certs);
        
        return "pages/certificates/staging_list";
    }
    
    @RequestMapping("/StagingCertificateView.do")
    public String stagingCertificateView(
            Model model, CertificatePromotionForm form, EngineConfiguration engineConfiguration)
                    throws NexusException,
                    KeyStoreException,
                    NoSuchAlgorithmException,
                    CertificateException,
                    IOException,
                    NoSuchProviderException {

        int nxCertificateId = form.getNxCertificateId();
        if (nxCertificateId == 0) {
            return stagingList(model, engineConfiguration);
        }
        List<CertificatePropertiesForm> certificateParts = new ArrayList<CertificatePropertiesForm>();
        List<PartnerPojo> localPartners = new ArrayList<PartnerPojo>();

        CertificatePojo cPojo = engineConfiguration.getCertificateByNxCertificateId(CertificateType.ALL.getOrdinal(), nxCertificateId);

        KeyStore jks = KeyStore.getInstance(CertificateUtil.DEFAULT_KEY_STORE, CertificateUtil.DEFAULT_JCE_PROVIDER);
        jks.load(new ByteArrayInputStream(cPojo.getBinaryData()), EncryptionUtil.decryptString(cPojo.getPassword()).toCharArray());
        if (jks != null) {
            Enumeration<String> aliases = jks.aliases();
            while (aliases.hasMoreElements()) {
                String tempAlias = (String) aliases.nextElement();
                if (jks.isKeyEntry(tempAlias)) {
                    Certificate[] certArray = jks.getCertificateChain(tempAlias);
                    if (certArray != null) {
                        for (int i = 0; i < certArray.length; i++) {
                            CertificatePropertiesForm certForm = new CertificatePropertiesForm();
                            X509Certificate x509 = (X509Certificate) certArray[i];
                            certForm.setCertificateProperties(x509);
                            certificateParts.add(certForm);
                        }
                    }
                }
            }
            localPartners = engineConfiguration.getPartners(Constants.PARTNER_TYPE_LOCAL, Constants.PARTNERCOMPARATOR);
        }
        form.setCertificateParts(certificateParts);
        form.setLocalPartners(localPartners);

        return "pages/certificates/staging_certificate_view";
    }
    
    @RequestMapping("/StagingImportCertificate.do")
    public String stagingImportCertificate(
            Model model, ProtectedFileAccessForm form, EngineConfiguration engineConfiguration) {
        
        return "pages/certificates/staging_import_certificate";
    }
    
    @RequestMapping("/StagingSaveCertificate.do")
    public String stagingSaveCertificate(
            Model model,
            @Valid ProtectedFileAccessForm form,
            BindingResult bindingResult,
            CertificatePromotionForm promotionForm,
            EngineConfiguration engineConfiguration)
                    throws KeyStoreException,
                    NoSuchAlgorithmException,
                    CertificateException,
                    NoSuchProviderException,
                    NexusException,
                    IOException {

        if (bindingResult.hasErrors()) {
            return stagingImportCertificate(model, form, engineConfiguration);
        }
        KeyStore jks = KeyStore.getInstance(CertificateUtil.DEFAULT_KEY_STORE, CertificateUtil.DEFAULT_JCE_PROVIDER);
        try {
            jks.load(form.getCertificate().getInputStream(), (form.getPassword() == null ? "" : form.getPassword()).toCharArray());
        } catch (CertificateException | IOException ex) {
            LOG.warn(ex);
            bindingResult.reject("certificates.import.invalidfile", "Invalid file");
        }
        if (bindingResult.hasErrors()) {
            return stagingImportCertificate(model, form, engineConfiguration);
        }

        List<CertificatePojo> certificates = new ArrayList<CertificatePojo>();
        CertificatePojo certificate = CertificateUtil.createPojoFromPKCS12(CertificateType.STAGING.getOrdinal(), jks, form.getPassword());
        certificates.add(certificate);

        // get installed CA cert's fingerprints
        Set<String> installedCaFingerPrints = new HashSet<String>();
        for (CertificatePojo cert : engineConfiguration.getCertificates(CertificateType.CA.getOrdinal(), null)) {
            byte[] data = cert.getBinaryData();
            if (data != null) {
                X509Certificate x509Certificate = CertificateUtil.getX509Certificate(data);
                if (x509Certificate != null) {
                    String fp = CertificateUtil.getMD5Fingerprint(x509Certificate);
                    if (fp != null) {
                        installedCaFingerPrints.add(fp);
                    }
                }
            }
        }

        // check if root certificate is in the CA cert list
        // if not, add it to the CA list
        Certificate[] chain = CertificateUtil.getCertificateChain(jks);
        if (chain != null && chain.length > 0) {
            for (int i = 0; i < chain.length; i++) {
                X509Certificate signer = (X509Certificate) chain[i];

                if (i > 0 || CertificateUtil.isSelfSigned(signer)) {
                    String fingerprint = CertificateUtil.getMD5Fingerprint(signer);
                    if (!installedCaFingerPrints.contains(fingerprint)) {
                        certificates.add(CertificateUtil.createPojoFromX509(signer, CertificateType.CA.getOrdinal()));
                    }
                }
            }
        }
        engineConfiguration.updateCertificates(certificates);

        return stagingCertificateView(model, promotionForm, engineConfiguration);
    }
    
    @RequestMapping("/StagingExportCertificate.do")
    public String stagingExportCertificate(ProtectedFileAccessForm form, EngineConfiguration engineConfiguration) throws NexusException {
        int nxCertificateId = form.getNxCertificateId();
        
        CertificatePojo cPojo = engineConfiguration.getCertificateByNxCertificateId(CertificateType.ALL.getOrdinal(), nxCertificateId);
        if (cPojo != null) {
            form.setAlias(cPojo.getName());
        }
        return "pages/certificates/staging_export_certificate";
    }
    
    @RequestMapping("/StagingStoreExported.do")
    public String stagingStoreExported(
            Model model, EngineConfiguration engineConfiguration, ProtectedFileAccessForm form, BindingResult bindingResult)
                    throws NexusException, NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException, NoSuchProviderException {

        int status = form.getStatus();
        int format = form.getFormat();
        int content = form.getContent();
        int nxCertificateId = form.getNxCertificateId();
        if (status == 1) {
            // Save with path
            String path = form.getCertificatePath();

            if (StringUtils.isBlank(path)) {
                bindingResult.rejectValue("certificatePath", "error.path.required");
                return stagingExportCertificate(form, engineConfiguration);
            }
            CertificatePojo cPojo = engineConfiguration.getCertificateByNxCertificateId(CertificateType.ALL.getOrdinal(), nxCertificateId);
            if (cPojo == null) {
                LOG.error("Certificate with nxId " + nxCertificateId + " not found.");
                return stagingExportCertificate(form, engineConfiguration);
            }
            KeyStore jks = KeyStore.getInstance(CertificateUtil.DEFAULT_KEY_STORE, CertificateUtil.DEFAULT_JCE_PROVIDER);
            jks.load(new ByteArrayInputStream(cPojo.getBinaryData()), EncryptionUtil.decryptString(cPojo.getPassword()).toCharArray());

            Certificate[] certs = CertificateUtil.getCertificateChain(jks);
            if (certs == null) {
                LOG.error("Certificate chain for certificate with nxId " + nxCertificateId + " not found.");
                return stagingExportCertificate(form, engineConfiguration);
            }

            File destFile = new File(path);
            if (content == 1) {
                X509Certificate cert = (X509Certificate) certs[0];
                byte[] data = null;
                if (format == ProtectedFileAccessForm.PEM) {
                    data = CertificateUtil.getPemData(cert).getBytes();
                } else if (format == ProtectedFileAccessForm.DER) {
                    data = cert.getEncoded();
                }
                FileOutputStream fos = new FileOutputStream(destFile);
                fos.write(data);
                fos.flush();
                fos.close();
                // ZIP
            } else if (content == 2) {
                FileOutputStream fos = new FileOutputStream(destFile);
                ZipOutputStream zos = new ZipOutputStream(fos);
                String ext = "";

                if (format == ProtectedFileAccessForm.PEM) {
                    ext = ".pem";
                } else {
                    ext = ".der";
                }
                ByteArrayOutputStream indexStream = new ByteArrayOutputStream();
                PrintWriter pw = new PrintWriter(indexStream);

                for (int i = 0; i < certs.length; i++) {
                    String certName = CertificateUtil.createCertificateId((X509Certificate) certs[i]);
                    String cn = CertificateUtil.getSubject((X509Certificate) certs[i], X509Name.CN);
                    String o = CertificateUtil.getSubject((X509Certificate) certs[i], X509Name.O);
                    String fingerprint = "NA";
                    byte[] resBuf;
                    try {
                        Digest digest = new MD5Digest();
                        resBuf = new byte[digest.getDigestSize()];
                        digest.update(certs[i].getEncoded(), 0, certs[i].getEncoded().length);
                        digest.doFinal(resBuf, 0);
                        fingerprint = new String(Hex.encode(resBuf));
                    } catch (CertificateEncodingException e1) {
                    }

                    ZipEntry ze = new ZipEntry(certName + ext);
                    zos.putNextEntry(ze);
                    pw.println("CommonName: " + cn);
                    pw.println("Organisation: " + o);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    pw.println("expires: " + sdf.format(((X509Certificate) certs[i]).getNotAfter()) + " - "
                            + CertificateUtil.getRemainingValidity((X509Certificate) certs[i]));
                    pw.println("FingerPrint: " + fingerprint);

                    if (i < certs.length - 1) {
                        pw.println("---------------------------------------------------------");
                    }

                    byte[] data = new byte[0];
                    if (format == ProtectedFileAccessForm.PEM) {
                        data = CertificateUtil.getPemData((X509Certificate) certs[i]).getBytes();
                    } else {
                        data = certs[i].getEncoded();
                    }
                    zos.write(data);
                }
                pw.flush();
                pw.close();

                ZipEntry index = new ZipEntry("index.txt");
                zos.putNextEntry(index);
                zos.write(indexStream.toByteArray());
                zos.flush();
                zos.close();
                fos.flush();
                fos.close();

                // PKCS 12 - full
            } else if (content == 3) {
                FileOutputStream fos = new FileOutputStream(destFile);
                fos.write(cPojo.getBinaryData());
                fos.flush();
                fos.close();
            }
        } else {
            if (content == 3) {
                if (StringUtils.isBlank(form.getPassword())) {
                    bindingResult.rejectValue("password", "error.password.required");
                    return stagingExportCertificate(form, engineConfiguration);
                } else if (StringUtils.isBlank(form.getPasswordRepeat())) {
                    bindingResult.rejectValue("passwordRepeat", "error.password.repeatRequired");
                    return stagingExportCertificate(form, engineConfiguration);
                } else if (!form.getPassword().equals(form.getPasswordRepeat())) {
                    bindingResult.rejectValue("passwordRepeat", "error.password.repeatMismatch");
                    return stagingExportCertificate(form, engineConfiguration);
                }
            }
            // Save as...
            model.addAttribute("downloadLinkUrl", "DataSaveAs.do?type=staging");
        }

        return stagingExportCertificate(form, engineConfiguration);
    }
    
    @RequestMapping("/StagingPromoteCertificate.do")
    public String stagingPromoteCertificate(CertificatePromotionForm form, Model model, EngineConfiguration engineConfiguration)
            throws NexusException, KeyStoreException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException, IOException {

        if ("changeServerIdentity".equals(form.getActionName())) {
            form.setActionName("promote");
            return stagingCertificateView(model, form, engineConfiguration);
        } else {
            int certificateId = form.getNxCertificateId();
            if (certificateId != 0) {
                PartnerPojo localPartner = engineConfiguration.getPartnerByNxPartnerId(form.getLocalNxPartnerId());
                CertificatePojo stagedCert = engineConfiguration.getCertificateByNxCertificateId(
                        CertificateType.STAGING.getOrdinal(), certificateId);

                if (localPartner != null && stagedCert != null) {
                    CertificatePojo certificate;
                    if (form.getReplaceNxCertificateId() == 0) {
                        certificate = new CertificatePojo();
                    } else {
                        certificate = engineConfiguration.getCertificateByNxCertificateId(
                                CertificateType.ALL.getOrdinal(), form.getReplaceNxCertificateId());
                    }
                    if (certificate != null) {
                        certificate.setType(CertificateType.LOCAL.getOrdinal());
                        certificate.setName(stagedCert.getName());
                        certificate.setModifiedDate(new Date());
                        certificate.setCreatedDate(new Date());
                        certificate.setBinaryData(stagedCert.getBinaryData());
                        certificate.setPassword(stagedCert.getPassword());
                        certificate.setPartner(localPartner);
                        localPartner.getCertificates().add(certificate);
                        engineConfiguration.updateCertificate(certificate);
                    }
                }
            }
        }

        return stagingList(model, engineConfiguration);
    }
    
    @RequestMapping("/StagingDeleteCertificate.do")
    public String stagingDeleteCertificate(Model model, CertificatePromotionForm form, EngineConfiguration engineConfiguration)
            throws ReferencedCertificateException,
            NexusException,
            NoSuchAlgorithmException,
            CertificateException,
            KeyStoreException,
            NoSuchProviderException,
            IOException {

        int nxCertificateId = form.getNxCertificateId();
        if (nxCertificateId != 0) {
            CertificatePojo cPojo = engineConfiguration.getCertificateByNxCertificateId(CertificateType.ALL.getOrdinal(), nxCertificateId);
            if (cPojo != null) {
                engineConfiguration.deleteCertificate(cPojo);
            }
        }
    
        return stagingList(model, engineConfiguration);
    }
}
