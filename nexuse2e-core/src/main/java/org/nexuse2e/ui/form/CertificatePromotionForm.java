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
package org.nexuse2e.ui.form;

import java.io.Serializable;
import java.util.List;

import org.nexuse2e.pojo.PartnerPojo;

/**
 * @author gesch
 *
 */
public class CertificatePromotionForm implements Serializable {

    private static final long               serialVersionUID = 536952753723321140L;

    private List<PartnerPojo>               localPartners;
    private List<CertificatePropertiesForm> certificateParts;
    private int                             localNxPartnerId;
    private int                             nxCertificateId  = 0;
    private String                          actionName;
    private int                             replaceNxCertificateId;

    public int getLocalNxPartnerId() {
        return localNxPartnerId;
    }

    public void setLocalNxPartnerId( int localNxPartnerId ) {
        this.localNxPartnerId = localNxPartnerId;
    }

    public List<PartnerPojo> getLocalPartners() {
        return localPartners;
    }

    public void setLocalPartners( List<PartnerPojo> localPartners ) {
        this.localPartners = localPartners;
    }

    public List<CertificatePropertiesForm> getCertificateParts() {
        return certificateParts;
    }

    public void setCertificateParts( List<CertificatePropertiesForm> certificateParts ) {
        this.certificateParts = certificateParts;
    }

    public int getNxCertificateId() {
        return nxCertificateId;
    }

    public void setNxCertificateId( int nxCertificateId ) {
        this.nxCertificateId = nxCertificateId;
    }

    public int getReplaceNxCertificateId() {
        return replaceNxCertificateId;
    }

    public void setReplaceNxCertificateId(int replaceNxCertificateId) {
        this.replaceNxCertificateId = replaceNxCertificateId;
    }

    public String getActionName() {
        return actionName;
    }
    
    public void setActionName( String action ) {
        this.actionName = action;
    }

}
