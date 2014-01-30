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
package org.nexuse2e.pojo;

// Generated 20.10.2006 15:50:01 by Hibernate Tools 3.2.0.beta6a

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Index;
import org.nexuse2e.Engine;

/**
 * ParticipantPojo generated by hbm2java
 */
@Entity
@Table(name = "nx_participant")
@XmlType(name = "ParticipantType")
@XmlAccessorType(XmlAccessType.NONE)
public class ParticipantPojo implements NEXUSe2ePojo {

	/**
     * 
     */
	private static final long serialVersionUID = -4038533703653628428L;

	// Fields

    @Id
    @Column(name = "nx_participant_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
	private int nxParticipantId;

    @ManyToOne(fetch = FetchType.EAGER)
    @Index(name = "ix_participant_2")
    @JoinColumn(name = "nx_partner_id", nullable = false)
	private PartnerPojo partner;

    @ManyToOne(fetch = FetchType.EAGER)
    @Index(name = "ix_participant_3")
    @JoinColumn(name = "nx_choreography_id", nullable = false)
	private ChoreographyPojo choreography;

    @ManyToOne(fetch = FetchType.EAGER)
    @Index(name = "ix_participant_4")
    @JoinColumn(name = "nx_local_partner_id", nullable = false)
	private PartnerPojo localPartner;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @Index(name = "ix_participant_5")
    @JoinColumn(name = "nx_connection_id", nullable = false)
	private ConnectionPojo connection;

    @Column(name = "created_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date              createdDate;

    @Column(name = "modified_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date              modifiedDate;

    @Column(name = "modified_nx_user_id", nullable = false)
    private int               modifiedNxUserId;

    @Column(name = "description", length = 64)
	private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @Index(name = "ix_participant_1")
    @JoinColumn(name = "nx_local_certificate_id")
	private CertificatePojo localCertificate;

    @Column(name = "char_encoding", length = 24)
	private String charEncoding;

    @Transient
    private int               nxPartnerId;
    @Transient
    private int               nxLocalPartnerId;
    @Transient
    private int               nxConnectionId;
    @Transient
    private int               nxLocalCertificateId;

	// Constructors

	/** default constructor */
	public ParticipantPojo() {

		createdDate = new Date();
		modifiedDate = createdDate;
	}

	/** minimal constructor */
	public ParticipantPojo(PartnerPojo partner, ChoreographyPojo choreography,
			PartnerPojo localPartner, ConnectionPojo connection,
			Date createdDate, Date modifiedDate, int modifiedNxUserId) {

		this.partner = partner;
		this.choreography = choreography;
		this.localPartner = localPartner;
		this.connection = connection;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.modifiedNxUserId = modifiedNxUserId;
	}

	/** full constructor */
	public ParticipantPojo(PartnerPojo partner, ChoreographyPojo choreography,
			PartnerPojo localPartner, ConnectionPojo connection,
			Date createdDate, Date modifiedDate, int modifiedNxUserId,
			String description) {

		this.partner = partner;
		this.choreography = choreography;
		this.localPartner = localPartner;
		this.connection = connection;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.modifiedNxUserId = modifiedNxUserId;
		this.description = description;
	}

	// Property accessors
	public int getNxParticipantId() {

		return this.nxParticipantId;
	}

	@XmlAttribute
	public void setNxParticipantId(int nxParticipantId) {

		this.nxParticipantId = nxParticipantId;
	}

	public int getNxId() {
		return nxParticipantId;
	}

	public void setNxId(int nxId) {
		this.nxParticipantId = nxId;
	}

	/**
	 * Required for JAXB
	 * 
	 * @return
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
	 * @param partnerId
	 */
	public void setNxPartnerId(int partnerId) {
		this.nxPartnerId = partnerId;
	}

	public PartnerPojo getPartner() {

		return this.partner;
	}

	public void setPartner(PartnerPojo partner) {

		this.partner = partner;
	}

	public ChoreographyPojo getChoreography() {

		return this.choreography;
	}

	public void setChoreography(ChoreographyPojo choreography) {

		this.choreography = choreography;
	}

	/**
	 * Required for JAXB
	 * 
	 * @return
	 */
	@XmlAttribute
	public int getNxLocalPartnerId() {

		if (this.localPartner != null) {
			return this.localPartner.getNxPartnerId();

		}
		return nxLocalPartnerId;
	}

	/**
	 * Required for JAXB
	 * 
	 * @param localPartnerId
	 */
	public void setNxLocalPartnerId(int localPartnerId) {
		this.nxLocalPartnerId = localPartnerId;
	}

	public PartnerPojo getLocalPartner() {

		return this.localPartner;
	}

	public void setLocalPartner(PartnerPojo localPartner) {

		this.localPartner = localPartner;
	}

	/**
	 * Required for JAXB
	 * 
	 * @return
	 */
	@XmlAttribute
	public int getNxConnectionId() {

		if (this.connection != null) {
			return this.connection.getNxConnectionId();

		}
		return nxConnectionId;
	}

	/**
	 * Required for JAXB
	 * 
	 * @param connectionId
	 */
	public void setNxConnectionId(int connectionId) {
		this.nxConnectionId = connectionId;
	}

	/**
	 * Required for JAXB
	 * 
	 * @return
	 */
	@XmlAttribute
	public int getNxLocalCertificateId() {
		if (this.localCertificate != null) {
			return this.localCertificate.getNxCertificateId();
		}
		return nxLocalCertificateId;
	}

	/**
	 * Required for JAXB
	 * 
	 * @param nxLocalCertificateId
	 */
	public void setNxLocalCertificateId(int nxLocalCertificateId) {
		this.nxLocalCertificateId = nxLocalCertificateId;
	}

	public ConnectionPojo getConnection() {

		return this.connection;
	}

	public void setConnection(ConnectionPojo connection) {

		this.connection = connection;
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
	public String getDescription() {

		return this.description;
	}

	public void setDescription(String description) {

		this.description = description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj instanceof ParticipantPojo) {
			if (nxParticipantId == 0) {
				return super.equals(obj);
			}
			return (this.getNxParticipantId() == ((ParticipantPojo) obj)
					.getNxParticipantId());
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		if (nxParticipantId == 0) {
			return super.hashCode();
		}

		return nxParticipantId;
	}

	/**
	 * @return the localCertificate
	 */
	public CertificatePojo getLocalCertificate() {

		return localCertificate;
	}

	/**
	 * @param localCertificate
	 *            the localCertificate to set
	 */
	public void setLocalCertificate(CertificatePojo localCertificate) {

		this.localCertificate = localCertificate;
	}

	/**
	 * @return the preferred encoding, if not configured, the engine setting is used. 
	 */
	public String getDefaultEncoding() {
		if (StringUtils.isEmpty(charEncoding)) {
			charEncoding = Engine.getInstance().getDefaultCharEncoding();
		}

		return charEncoding;
	}

	/**
	 * Method is required for hibernate. Its recommended to use the getDefaultEncoding method for accessing this value
	 * @return the charEncoding
	 */
	public String getCharEncoding() {
		return charEncoding;
	}

	/**
	 * @param charEncoding
	 */
	public void setCharEncoding(String charEncoding) {
		this.charEncoding = charEncoding;
	}
}
