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

// Generated 15.12.2006 16:07:02 by Hibernate Tools 3.2.0.beta6a

import java.util.Date;

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

import org.hibernate.annotations.Index;

/**
 * UserPojo generated by hbm2java
 */
@Entity
@Table(name = "nx_user")
@XmlType(name = "UserType")
@XmlAccessorType(XmlAccessType.NONE)
public class UserPojo implements NEXUSe2ePojo {

    private static final long serialVersionUID = 4646298773705177406L;

    // Fields    

    @Id
    @Column(name = "nx_user_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int               nxUserId;

    @Column(name = "login_name", length = 64, nullable = false)
    private String            loginName;

    @Column(name = "first_name", length = 64, nullable = false)
    private String            firstName;

    @Column(name = "middle_name", length = 64, nullable = true)
    private String            middleName;

    @Column(name = "last_name", length = 64, nullable = false)
    private String            lastName;

    @Column(name = "password", length = 64, nullable = false)
    private String            password;

    @Column(name = "created_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date              createdDate;

    @Column(name = "modified_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date              modifiedDate;

    @Column(name = "modified_nx_user_id", nullable = false)
    private int               modifiedNxUserId;

    @Column(name = "active", nullable = false)
    private boolean           active;

    @Column(name = "visible", nullable = false)
    private boolean           visible;

    @ManyToOne(fetch = FetchType.EAGER)
    @Index(name = "ix_user_1")
    @JoinColumn(name = "nx_role_id", nullable = true)
    private RolePojo          role;

    @Transient
    private int               nxRoleId;
    
    // Constructors

    /** default constructor */
    public UserPojo() {
        createdDate = new Date();
        modifiedDate = createdDate;
    }

    /** minimal constructor */
    public UserPojo( String loginName, String firstName, String lastName, String password, Date createdDate,
            Date modifiedDate, int modifiedNxUserId, boolean active, boolean visible ) {

        this.loginName = loginName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.modifiedNxUserId = modifiedNxUserId;
        this.active = active;
        this.visible = visible;
    }

    /** full constructor */
    public UserPojo( String loginName, String firstName, String middleName, String lastName, String password,
            Date createdDate, Date modifiedDate, int modifiedNxUserId, boolean active, boolean visible, RolePojo role ) {

        this.loginName = loginName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.password = password;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.modifiedNxUserId = modifiedNxUserId;
        this.active = active;
        this.visible = visible;
        this.role = role;
    }

    // Property accessors
    @XmlAttribute
    public int getNxUserId() {

        return this.nxUserId;
    }

    public void setNxUserId( int nxUserId ) {

        this.nxUserId = nxUserId;
    }

    public int getNxId() {
        return nxUserId;
    }
    
    public void setNxId( int nxId ) {
        this.nxUserId = nxId;
    }
    
    @XmlAttribute
    public String getLoginName() {

        return this.loginName;
    }

    public void setLoginName( String loginName ) {

        this.loginName = loginName;
    }

    @XmlAttribute
    public String getFirstName() {

        return this.firstName;
    }

    public void setFirstName( String firstName ) {

        this.firstName = firstName;
    }

    @XmlAttribute
    public String getMiddleName() {

        return this.middleName;
    }

    public void setMiddleName( String middleName ) {

        this.middleName = middleName;
    }

    @XmlAttribute
    public String getLastName() {

        return this.lastName;
    }

    public void setLastName( String lastName ) {

        this.lastName = lastName;
    }

    @XmlAttribute
    public String getPassword() {

        return this.password;
    }

    public void setPassword( String password ) {

        this.password = password;
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

    @XmlAttribute
    public boolean isActive() {

        return this.active;
    }

    public void setActive( boolean active ) {

        this.active = active;
    }

    public boolean isVisible() {

        return this.visible;
    }

    @XmlAttribute
    public void setVisible( boolean visible ) {

        this.visible = visible;
    }

    public RolePojo getRole() {

        return this.role;
    }

    public void setRole( RolePojo role ) {

        this.role = role;
    }

    /**
     * Required for JAXB
     * @return
     */
    @XmlAttribute
    public int getNxRoleId() {
        if ( this.role != null ) {
            return this.role.getNxRoleId();

        }
        return nxRoleId;
    }
    
    /**
     * Required for JAXB
     * @param roleId
     */
    public void setNxRoleId( int roleId ) {
        this.nxRoleId = roleId;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {

        if ( !( obj instanceof UserPojo ) ) {
            return false;
        }

        if ( nxUserId == 0 ) {
            return super.equals( obj );
        }

        return nxUserId == ( (UserPojo) obj ).nxUserId;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        if ( nxUserId == 0 ) {
            return super.hashCode();
        }

        return nxUserId;
    }
}
