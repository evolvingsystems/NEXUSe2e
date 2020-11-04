package org.nexuse2e.reporting;

import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.PartnerPojo;
import org.nexuse2e.ui.form.CollaborationPartnerForm;

public class StatisticsCertificate implements Comparable<StatisticsCertificate> {
    private final String name;
    private final String timeUntilExpiry;
    private final int nxCertificateId;
    private final String configuredFor;
    private int nxPartnerId;

    public StatisticsCertificate(CertificatePojo certificatePojo) {
        CollaborationPartnerForm form = new CollaborationPartnerForm();
        CollaborationPartnerForm.Certificate certificate = form.new Certificate();
        certificate.setProperties(certificatePojo);
        this.timeUntilExpiry = format(certificate.getRemaining());
        this.name = certificatePojo.getName();
        this.nxCertificateId = certificatePojo.getNxCertificateId();
        PartnerPojo partner = certificatePojo.getPartner();
        if (partner != null) {
            this.configuredFor = partner.getName();
            this.nxPartnerId = partner.getNxPartnerId();
        } else {
            this.configuredFor = "Local";
        }
    }

    public String getConfiguredFor() {
        return configuredFor;
    }

    public String getName() {
        return name;
    }

    public int getNxCertificateId() {
        return nxCertificateId;
    }

    public int getNxPartnerId() {
        return nxPartnerId;
    }

    public String getTimeUntilExpiry() {
        return timeUntilExpiry;
    }

    private String format(String withBrackets) {
        if (withBrackets.isEmpty()) {
            return "expired";
        }
        return withBrackets.replaceAll("[\\(\\)]", "");
    }

    @Override
    public int compareTo(StatisticsCertificate otherCertificate) {
        if (!this.configuredFor.equals(otherCertificate.configuredFor)) {
            if (this.configuredFor.equals("Local")) {
                return -1;
            }
            if (otherCertificate.configuredFor.equals("Local")) {
                return 1;
            }
            return this.configuredFor.compareTo(otherCertificate.configuredFor);
        }
        if (!this.name.equals(otherCertificate.name)) {
            return this.name.compareTo(otherCertificate.name);
        }
        return this.nxCertificateId - otherCertificate.nxCertificateId;
    }
}
