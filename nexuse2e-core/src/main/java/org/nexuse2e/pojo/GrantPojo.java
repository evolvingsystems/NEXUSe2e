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

// Generated 15.12.2006 16:07:02 by Hibernate Tools 3.2.0.beta6a

import org.hibernate.annotations.GenericGenerator;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 *
 */

@Entity
@Table(name = "nx_grant")
@XmlType(name = "GrantType")
@XmlAccessorType(XmlAccessType.NONE )
public class GrantPojo implements NEXUSe2ePojo {

    /**
     * 
     */
    private static final long serialVersionUID = -6954829251364806284L;
    @Access(AccessType.PROPERTY)
    @Id
    @Column(name = "nx_grant_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private int               nxGrantId;

    @Column(name = "target", length = 64, nullable = false)
    private String            target;

    @Column(name = "created_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date              createdDate;

    @Column(name = "modified_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date              modifiedDate;

    @Column(name = "modified_nx_user_id", nullable = false)
    private int               modifiedNxUserId;

    
	/** default constructor */
    public GrantPojo() {
        createdDate = new Date();
        modifiedDate = createdDate;
    }

    /** minimal constructor */
    public GrantPojo( String target, Date createdDate, Date modifiedDate, int modifiedNxUserId ) {

        this.target = target;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.modifiedNxUserId = modifiedNxUserId;
    }

    // Property accessors
    public int getNxGrantId() {

        return this.nxGrantId;
    }

    public void setNxGrantId( int nxGrantId ) {

        this.nxGrantId = nxGrantId;
    }

    public int getNxId() {
        return nxGrantId;
    }
    
    public void setNxId( int nxId ) {
        this.nxGrantId = nxId;
    }
    
    @XmlAttribute
    public String getTarget() {

        return this.target;
    }

    public void setTarget( String target ) {

        this.target = target;
    }

    public Date getCreatedDate() {

        return this.createdDate;
    }

    public void setCreatedDate( Date createdDate ) {

        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {

        return this.modifiedDate;
    }

    public void setModifiedDate( Date modifiedDate ) {

        this.modifiedDate = modifiedDate;
    }

    public int getModifiedNxUserId() {

        return this.modifiedNxUserId;
    }

    public void setModifiedNxUserId( int modifiedNxUserId ) {

        this.modifiedNxUserId = modifiedNxUserId;
    }
    
    
}
