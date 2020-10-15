package org.nexuse2e.reporting;

import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.ui.form.CertificatePropertiesForm;
import org.nexuse2e.ui.form.CollaborationPartnerForm;

public class CertificateStub {
    private String name;
    private String remaining;

    public CertificateStub(CertificatePojo certificatePojo) {
        CollaborationPartnerForm form = new CollaborationPartnerForm();
        CollaborationPartnerForm.Certificate certificate = form.new Certificate();
        certificate.setProperties(certificatePojo);
        this.remaining = removeBrackets(certificate.getRemaining());
        this.name = certificatePojo.getName();
    }

    public CertificateStub(CertificatePropertiesForm certificatePropertiesForm) {
        this.remaining = removeBrackets(certificatePropertiesForm.getTimeRemaining());
        this.name = certificatePropertiesForm.getCommonName();
    }

    public String getName() {
        return name;
    }

    public String getRemaining() {
        return remaining;
    }

    private String removeBrackets(String withBrackets) {
        return withBrackets.replaceAll("[\\(\\)]", "");
    }
}
