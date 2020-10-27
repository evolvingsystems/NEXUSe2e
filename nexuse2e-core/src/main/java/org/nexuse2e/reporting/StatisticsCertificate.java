package org.nexuse2e.reporting;

import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.ui.form.CertificatePropertiesForm;
import org.nexuse2e.ui.form.CollaborationPartnerForm;

public class StatisticsCertificate {
    private String name;
    private String timeUntilExpiry;
    private int nxCertificateId;

    public StatisticsCertificate(CertificatePojo certificatePojo) {
        CollaborationPartnerForm form = new CollaborationPartnerForm();
        CollaborationPartnerForm.Certificate certificate = form.new Certificate();
        certificate.setProperties(certificatePojo);
        this.timeUntilExpiry = removeBrackets(certificate.getRemaining());
        this.name = certificatePojo.getName();
        this.nxCertificateId = certificatePojo.getNxCertificateId();
    }

    public StatisticsCertificate(CertificatePropertiesForm certificatePropertiesForm) {
        this.timeUntilExpiry = removeBrackets(certificatePropertiesForm.getTimeRemaining());
        this.name = certificatePropertiesForm.getCommonName();
        this.nxCertificateId = certificatePropertiesForm.getNxCertificateId();
    }

    public String getName() {
        return name;
    }

    public String getTimeUntilExpiry() {
        return timeUntilExpiry;
    }

    public int getNxCertificateId() {
        return nxCertificateId;
    }

    private String removeBrackets(String withBrackets) {
        return withBrackets.replaceAll("[\\(\\)]", "");
    }
}
