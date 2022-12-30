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
package org.nexuse2e.pojo;

import java.util.Date;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.nexuse2e.configuration.CertificateType;

@Entity
@Table(name = "nx_certificate")
@XmlType(name = "CertificateType")
@XmlAccessorType(XmlAccessType.NONE)
public class CertificatePojo implements NEXUSe2ePojo {

    
    /**
     * 
     */
    private static final long serialVersionUID = 6193340972799640301L;

    @Access(AccessType.PROPERTY)
    @Id
    @Column(name = "nx_certificate_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private int               nxCertificateId;

    @Column(name = "type", nullable = false)
    private int               type;
    
    @Column(name = "password", length = 64)
    private String            password;
    
    @Column(name = "created_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date              createdDate;
    
    @Column(name = "modified_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date              modifiedDate;

    @Column(name = "modified_nx_user_id", nullable = false)
    private int               modifiedNxUserId;

    @ManyToOne(fetch = FetchType.EAGER)
    @Index(name = "ix_certificate_1")
    @JoinColumn(name = "nx_partner_id", nullable = true)
    private PartnerPojo       partner;

    @Column(name = "name", length = 512, nullable = false)
    private String            name;

    @Column(name = "description", length = 256)
    private String            description;

    @Column(name = "binary_data", nullable = true)
    @Lob
    private byte[]            binaryData;
    
    @Transient
    private int               nxPartnerId;

    // Constructors

    /** default constructor */
    public CertificatePojo() {

        createdDate = new Date();
        modifiedDate = createdDate;
    }

    /** minimal constructor */
    public CertificatePojo(int type, Date createdDate, Date modifiedDate, int modifiedNxUserId, PartnerPojo partner, String name, byte[] binaryData) {

        this.type = type;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.modifiedNxUserId = modifiedNxUserId;
        this.partner = partner;
        this.name = name;
        this.binaryData = binaryData;
    }

    /** full constructor */
    public CertificatePojo(int type, String password, Date createdDate, Date modifiedDate, int modifiedNxUserId, PartnerPojo partner, String name,
            String description, byte[] binaryData) {

        this.type = type;
        this.password = password;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.modifiedNxUserId = modifiedNxUserId;
        this.partner = partner;
        this.name = name;
        this.description = description;
        this.binaryData = binaryData;
    }

    // Property accessors
    @XmlAttribute
    public int getNxCertificateId() {

        return this.nxCertificateId;
    }

    public void setNxCertificateId(int nxCertificateId) {

        this.nxCertificateId = nxCertificateId;
    }

    public int getNxId() {
        return nxCertificateId;
    }

    public void setNxId(int nxId) {
        this.nxCertificateId = nxId;
    }

    @XmlAttribute
    public int getType() {

        return this.type;
    }

    public void setType(int type) {

        this.type = type;
    }

    @XmlAttribute
    public String getPassword() {

        return this.password;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    public Date getCreatedDate() {

        return this.createdDate;
    }

    public void setCreatedDate(Date createdDate) {

        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {

        return this.modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {

        this.modifiedDate = modifiedDate;
    }

    public int getModifiedNxUserId() {

        return this.modifiedNxUserId;
    }

    public void setModifiedNxUserId(int modifiedNxUserId) {

        this.modifiedNxUserId = modifiedNxUserId;
    }

    @XmlAttribute
    public String getName() {

        return this.name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getDescription() {

        return this.description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    @XmlElement
    public byte[] getBinaryData() {

        return this.binaryData;
    }

    public void setBinaryData(byte[] binaryData) {

        this.binaryData = binaryData;
    }

    /**
     * @return the partner
     */
    public PartnerPojo getPartner() {

        return partner;
    }

    /**
     * @param partner
     *            the partner to set
     */
    public void setPartner(PartnerPojo partner) {

        this.partner = partner;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof CertificatePojo)) {
            return false;
        }
        if (nxCertificateId == 0) {
            return super.equals(obj);
        }

        return nxCertificateId == ((CertificatePojo) obj).nxCertificateId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        if (nxCertificateId == 0) {
            return super.hashCode();
        }

        return nxCertificateId;
    }

    /**
     * Incuded Pojo Types: CA and PARTNER
     * 
     * @return
     */
    public boolean isX509() {

        if (getType() == CertificateType.CA.getOrdinal() || getType() == CertificateType.PARTNER.getOrdinal()) {
            return true;
        }
        return false;
    }

    /**
     * Included Pojo Types: CERT_PART, STAGING and LOCAL
     * 
     * @return
     */
    public boolean isPKCS12() {

        if (getType() == CertificateType.STAGING.getOrdinal() || getType() == CertificateType.LOCAL.getOrdinal()) {
            return true;
        }
        return false;
    }

    /**
     * Included Pojo Types: REQUEST
     * 
     * @return
     */
    public boolean isPKCS10() {

        if (getType() == CertificateType.REQUEST.getOrdinal()) {
            return true;
        }
        return false;
    }

    /**
     * Required for JAXB
     */
    @XmlAttribute
    public int getNxPartnerId() {
        if (this.partner != null) {
            return this.partner.getNxPartnerId();
        }
        return nxPartnerId;
    }

    /**
     * Required for JAXB
     * 
     * @param nxPartnerId
     */
    public void setNxPartnerId(int nxPartnerId) {
        this.nxPartnerId = nxPartnerId;
    }
}
