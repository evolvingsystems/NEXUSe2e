package org.nexuse2e.ui.controller.certificate;

import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.CertificateType;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.ui.form.RequestButtonStateForm;
import org.nexuse2e.util.CertificateUtil;
import org.springframework.stereotype.Controller;
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
    public String requestOverview(RequestButtonStateForm form, EngineConfiguration engineConfiguration) throws NexusException {

        CertificatePojo certificateRequest = engineConfiguration.getFirstCertificateByType(CertificateType.REQUEST.getOrdinal(), false);
        CertificatePojo certificateKey = null;
        if (certificateRequest == null) {
            LOG.error("no certificate request found in database");
        } else {
            certificateKey = engineConfiguration.getFirstCertificateByType(CertificateType.PRIVATE_KEY.getOrdinal(), true);
        }
        form.setCreateRequest(true);
        form.setImportCert(false);
        form.setShowRequest(false);
        form.setDeleteRequest(true);
        if (certificateRequest != null && certificateKey != null) {
            try {
                CertificateUtil.getPKCS10Request(certificateRequest);
                form.setCreateRequest(false);
                form.setImportCert(true);
                form.setShowRequest(true);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        if (certificateRequest == null && certificateKey == null) {
            form.setDeleteRequest(false);
        }

        form.setImportBackup(true);
        form.setExportPKCS12(true);
        form.setExportRequest(true);

        return "pages/certificates/request_overview";
    }
}
