package org.nexuse2e.reporting;

import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.ui.form.CertificatePropertiesForm;
import org.nexuse2e.ui.form.CollaborationPartnerForm;

public class StatisticsCertificate {
    private String name;
    private String remaining;
    private int nxCertificateId;

    public StatisticsCertificate(CertificatePojo certificatePojo) {
        CollaborationPartnerForm form = new CollaborationPartnerForm();
        CollaborationPartnerForm.Certificate certificate = form.new Certificate();
        certificate.setProperties(certificatePojo);
        this.remaining = removeBrackets(certificate.getRemaining());
        this.name = certificatePojo.getName();
        this.nxCertificateId = certificatePojo.getNxCertificateId();
    }

    public StatisticsCertificate(CertificatePropertiesForm certificatePropertiesForm) {
        this.remaining = removeBrackets(certificatePropertiesForm.getTimeRemaining());
        this.name = certificatePropertiesForm.getCommonName();
        this.nxCertificateId = certificatePropertiesForm.getNxCertificateId();
    }

    public String getName() {
        return name;
    }

    public String getRemaining() {
        return remaining;
    }

    public int getNxCertificateId() {
        return nxCertificateId;
    }

    private String removeBrackets(String withBrackets) {
        return withBrackets.replaceAll("[\\(\\)]", "");
    }
}
