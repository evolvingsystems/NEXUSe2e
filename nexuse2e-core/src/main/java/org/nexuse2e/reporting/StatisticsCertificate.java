/**
 *  NEXUSe2e Business Messaging Open Source
 *  Copyright 2000-2021, direkt gruppe GmbH
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation version 3 of
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
    private final String validity;
    private int remainingDayCount;

    public StatisticsCertificate(CertificatePojo certificatePojo, boolean local) {
        CollaborationPartnerForm form = new CollaborationPartnerForm();
        CollaborationPartnerForm.Certificate certificate = form.new Certificate();
        certificate.setProperties(certificatePojo);
        this.timeUntilExpiry = format(certificate.getRemaining());
        this.name = certificatePojo.getName();
        this.nxCertificateId = certificatePojo.getNxCertificateId();
        if (local) {
            this.configuredFor = "Local";
        } else {
            PartnerPojo partner = certificatePojo.getPartner();
            this.configuredFor = partner.getName();
            this.nxPartnerId = partner.getNxPartnerId();
        }
        this.validity = certificate.getValidity();
        this.remainingDayCount = certificate.getRemainingDayCount();
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

    public String getValidity() {
        return validity;
    }

    public int getRemainingDayCount() {
        return remainingDayCount;
    }

    public void setRemainingDayCount(int remainingDayCount) {
        this.remainingDayCount = remainingDayCount;
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
