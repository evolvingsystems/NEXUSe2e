package org.nexuse2e.ui.controller.certificate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipInputStream;

import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.CertificateType;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.CertificatePojo;
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
        model.addAttribute("importCert", false);
        model.addAttribute("showRequest", false);
        model.addAttribute("exportRequest", false);
        model.addAttribute("exportPKCS12", false);
        model.addAttribute("deleteRequest", true);
        if (certificateRequest != null && certificateKey != null) {
            try {
                CertificateUtil.getPKCS10Request(certificateRequest);
                model.addAttribute("createRequest", false);
                model.addAttribute("importCert", true);
                model.addAttribute("showRequest", true);
                model.addAttribute("exportRequest", true);
                model.addAttribute("exportPKCS12", true);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        if (certificateRequest == null && certificateKey == null) {
            model.addAttribute("deleteRequest", false);
        }

        model.addAttribute("importBackup", true);

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
}
