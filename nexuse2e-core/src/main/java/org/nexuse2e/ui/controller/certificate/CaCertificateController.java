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
import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.CertificateType;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ReferencedCertificateException;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.ui.form.CertificatePropertiesForm;
import org.nexuse2e.ui.form.ProtectedFileAccessForm;
import org.nexuse2e.util.CertificateUtil;
import org.nexuse2e.util.EncryptionUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for CA certificate (trust store) maintenance
 *
 * @author Jonas Reese
 */
@Controller
public class CaCertificateController {

    protected static Logger LOG = Logger.getLogger(CaCertificateController.class);

    @RequestMapping("/CACertificatesList.do")
    public String caCertificatesList(Model model, EngineConfiguration engineConfiguration) throws NexusException {

        List<CertificatePropertiesForm> certs = new ArrayList<CertificatePropertiesForm>();
        List<CertificatePojo> certificates = engineConfiguration.getCertificates(CertificateType.CA.getOrdinal(), Constants.CERTIFICATECOMPARATOR);
        if (certificates != null) {
            for (CertificatePojo certificate : certificates) {
                byte[] data = certificate.getBinaryData();
                if (data != null) {
                    X509Certificate x509Certificate = CertificateUtil.getX509Certificate(data);
                    if (x509Certificate != null) {
                        CertificatePropertiesForm form = new CertificatePropertiesForm();
                        form.setCertificateProperties(x509Certificate);
                        form.setAlias(certificate.getName());
                        form.setNxCertificateId(certificate.getNxCertificateId());
                        certs.add(form);
                    }
                }
            }
        }

        model.addAttribute("collection", certs);

        return "pages/certificates/ca_certificate_list";
    }
    
    @RequestMapping("/CACertificateImportKeyStore.do")
    public String caCertificateImportKeyStore(ProtectedFileAccessForm form) {
        return "pages/certificates/ca_certificate_import";
    }
    
    @RequestMapping("/CACertificateSaveKeyStore.do")
    public String caCertificateSaveKeyStore(
            Model model, ProtectedFileAccessForm form, BindingResult result, EngineConfiguration engineConfiguration)
                    throws KeyStoreException, NexusException {

        String pwd = form.getPassword();

        if (form.getCertificate() == null || form.getCertificate() == null || form.getCertificate().getFileItem() == null) {
            result.rejectValue("certificate", "cacerts.certfilenotfound", "No data for certificate file submitted!");
        }
        
        byte[] data = form.getCertificate().getFileItem().get();

        if (data == null || data.length == 0) {
            result.rejectValue("certificate", "keystore.import.nofile", "No data for certificate file submitted!");
        }

        if (result.hasErrors()) {
            return caCertificateImportKeyStore(form);
        }
        
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        KeyStore jks = KeyStore.getInstance("JKS");
        try {
            jks.load(bais, pwd.toCharArray());
            Enumeration<String> e = jks.aliases();
            List<CertificatePojo> certs = new ArrayList<CertificatePojo>();
    
            while (e.hasMoreElements()) {
                String alias = (String) e.nextElement();
                X509Certificate cert = (X509Certificate) jks.getCertificate(alias);
                CertificatePojo existingPojo = engineConfiguration.getCertificateByName(CertificateType.CA.getOrdinal(), alias);
                if (existingPojo == null) {
                    CertificatePojo certPojo = new CertificatePojo();
                    certPojo.setType(CertificateType.CA.getOrdinal());
                    certPojo.setName(alias);
                    certPojo.setBinaryData(cert.getEncoded());
                    certPojo.setPassword("");
                    certPojo.setCreatedDate(new Date());
                    certPojo.setModifiedDate(new Date());
                    LOG.debug("importing certificate: " + certPojo.getName());
    
                    certs.add(certPojo);
                } else {
                    LOG.info("Alias: " + alias + " already imported");
                }
            }
    
            List<CertificatePojo> metaPojos = engineConfiguration.getCertificates(CertificateType.CACERT_METADATA.getOrdinal(), null);
    
            if (metaPojos == null || metaPojos.size() == 0) {
                CertificatePojo certPojo = new CertificatePojo();
                certPojo.setType(CertificateType.CACERT_METADATA.getOrdinal());
                certPojo.setName("CaKeyStoreData");
                certPojo.setPassword(EncryptionUtil.encryptString(pwd));
                certPojo.setCreatedDate(new Date());
                certPojo.setModifiedDate(new Date());
    
                certs.add(certPojo);
            }
    
            engineConfiguration.updateCertificates(certs);
        } catch (Exception ex) {
            LOG.warn(ex);
            if (ex.getCause() instanceof UnrecoverableKeyException) {
                result.rejectValue("password", "keystore.import.wrongpassword");
            } else {
                result.reject("keystore.import.error", new Object[] { ex.getMessage() }, null);
            }
            return caCertificateImportKeyStore(form);
        }

        return caCertificatesList(model, engineConfiguration);
    }
    
    @RequestMapping("/CACertificateView.do")
    public String caCertificateView(
            Model model, CertificatePropertiesForm form, EngineConfiguration engineConfiguration)
                    throws NexusException {

        CertificatePojo cPojo = engineConfiguration.getCertificateByNxCertificateId(CertificateType.CA.getOrdinal(), form.getNxCertificateId());
        if (cPojo != null) {
            byte[] data = cPojo.getBinaryData();
            X509Certificate x509Certificate = CertificateUtil.getX509Certificate(data);
            form.setCertificateProperties(x509Certificate);
            form.setAlias(cPojo.getName());
            form.setNxCertificateId(cPojo.getNxCertificateId());
            return "pages/certificates/ca_certificate_view";
        }

        return caCertificatesList(model, engineConfiguration);
    }
    
    @RequestMapping("/CACertificateDelete.do")
    public String caCertificateDelete(Model model, CertificatePropertiesForm form, EngineConfiguration engineConfiguration)
            throws ReferencedCertificateException, NexusException {

        CertificatePojo cPojo = engineConfiguration.getCertificateByNxCertificateId(CertificateType.CA.getOrdinal(), form.getNxCertificateId());

        if (cPojo != null) {
            engineConfiguration.deleteCertificate(cPojo);
        }
        
        return caCertificatesList(model, engineConfiguration);
    }
    
    @RequestMapping("/CACertificateAddSingleCert.do")
    public String caCertificateAddSingleCert(ProtectedFileAccessForm form) {
        
        return "pages/certificates/ca_certificate_add";
    }
    
    @RequestMapping("/CACertificateVerifyAddCert.do")
    public String caCertificateVerifyAddCert(
            Model model, @Valid ProtectedFileAccessForm form, BindingResult result, EngineConfiguration engineConfiguration)
                    throws NexusException {
        
        if (form.getCertificate() == null || form.getCertificate() == null || form.getCertificate().getFileItem() == null) {
            result.rejectValue("certificate", "certificates.import.nofile", "No data for certificate file submitted!");
            return caCertificateAddSingleCert(form);
        }
        
        byte[] data = form.getCertificate().getFileItem().get();

        if (data == null || data.length == 0) {
            result.rejectValue("certificate", "certificates.import.nofile", "No data for certificate file submitted!");
            return caCertificateAddSingleCert(form);
        }

        if (CertificateUtil.getX509Certificate(data) == null) {
            result.rejectValue("certificate", "certificates.import.invalidfile");
            return caCertificateAddSingleCert(form);
        }
        
        if (StringUtils.isBlank(form.getAlias())) {
            result.rejectValue("alias", "certificates.import.alias.required");
            return caCertificateAddSingleCert(form);
        }

        CertificatePojo certificate = new CertificatePojo();
        certificate.setName(form.getAlias());
        certificate.setType(CertificateType.CA.getOrdinal());
        certificate.setPassword("");
        certificate.setCreatedDate(new Date());

        certificate.setModifiedDate(new Date());
        certificate.setBinaryData(data);
        engineConfiguration.updateCertificate(certificate);

        return caCertificatesList(model, engineConfiguration);
    }
    
    @RequestMapping("/CACertificateExportKeyStore.do")
    public String caCertificateExportKeyStore(ProtectedFileAccessForm form) {

        form.setCertificatePath(Engine.getInstance().getCacertsPath());
        return "pages/certificates/ca_certificate_export";
    }
    
    @RequestMapping("/CACertificateFinishExport.do")
    public String caCertificateFinishExport(
            Model model, @Valid ProtectedFileAccessForm form, BindingResult result, EngineConfiguration engineConfiguration)
                    throws NexusException {

        if (form.getStatus() != 3) {
            String path = Engine.getInstance().getCacertsPath();
            if (form.getStatus() == 2) {
                path = form.getCertificatePath();
            }

            LOG.debug("CA certificate export path: " + path);

            CertificatePojo cPojo = engineConfiguration.getFirstCertificateByType(CertificateType.CACERT_METADATA.getOrdinal(), true);
            String password = "changeit";
            if (cPojo == null) {
                LOG.error("Error retrieving CA meta data!");
            } else {
                password = EncryptionUtil.decryptString(cPojo.getPassword());
            }
            LOG.trace("Using password: " + password);

            File certFile = new File(path);
            try (FileOutputStream fos = new FileOutputStream(certFile)) {
                List<CertificatePojo> caCertificates = engineConfiguration.getCertificates(CertificateType.CA.getOrdinal(), null);
                KeyStore jks = CertificateUtil.generateKeyStoreFromPojos(caCertificates);
                jks.store(fos, new String(password).toCharArray());
            } catch (Exception e) {
                LOG.warn("Exception saving keystore", e);
                result.reject("generic.error", new Object[] { e.getMessage() }, null);
                return caCertificateExportKeyStore(form);
            }
        }

        return caCertificatesList(model, engineConfiguration);
    }
    
    @RequestMapping("/CACertificateChangePWD.do")
    public String caCertificateChangePwd(ProtectedFileAccessForm form) {
        
        return "pages/certificates/ca_certificate_change_pwd";
    }
    
    @RequestMapping("/CACertificateSavePWD.do")
    public String caCertificateSavePwd(
            Model model, @Valid ProtectedFileAccessForm form, BindingResult result, EngineConfiguration engineConfiguration)
                    throws NexusException {

        if (result.hasErrors()) {
            return caCertificateChangePwd(form);
        }
        
        List<CertificatePojo> certificates = engineConfiguration.getCertificates(CertificateType.CACERT_METADATA.getOrdinal(), null);

        String encPwd = EncryptionUtil.encryptString(form.getPassword());
        CertificatePojo certificate = null;
        if (certificates != null && certificates.size() > 0) {
            certificate = certificates.get(0);
        } else {
            certificate = new CertificatePojo();
            certificate.setType(CertificateType.CACERT_METADATA.getOrdinal());
            certificate.setCreatedDate(new Date());
            certificate.setModifiedDate(new Date());
            certificate.setName("CaKeyStoreData");
        }

        certificate.setPassword(encPwd);
        certificate.setBinaryData(new byte[0]);
        engineConfiguration.updateCertificate(certificate);

        return caCertificatesList(model, engineConfiguration);
    }
}
