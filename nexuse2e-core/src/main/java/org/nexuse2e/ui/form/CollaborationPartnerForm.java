/**
 * NEXUSe2e Business Messaging Open Source
 * Copyright 2000-2009, Tamgroup and X-ioma GmbH
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation version 2.1 of
 * the License.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.nexuse2e.ui.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.validation.constraints.Size;

import org.nexuse2e.pojo.PartnerPojo;

/**
 * @author gesch
 * 
 */
public class CollaborationPartnerForm implements Serializable {

    private static final long                 serialVersionUID = 6867989805361808373L;
    private String                            name;
    private String                            company;
    private int                               nxPartnerId      = 0;
    private String                            partnerId;
    private String                            partnerIdType;
    private int                               type;
    private String                            address1;
    private String                            address2;
    private String                            city;
    private String                            state;
    private String                            zip;
    private String                            country;
    private Date                              created;
    private Date                              lastModified;
    private Collection<String>                choreographies   = new ArrayList<String>();
    private Collection<PartnerPojo>           contacts         = new ArrayList<PartnerPojo>();
    private Collection<PartnerConnectionForm> connections      = new ArrayList<PartnerConnectionForm>();
    private Collection<CertificatePropertiesForm> certificates = new ArrayList<CertificatePropertiesForm>();

    public void cleanSettings() {

        setName(null);
        setCompany(null);
        setPartnerId(null);
        setPartnerIdType(null);
        setType(0);
        setAddress1(null);
        setAddress2(null);
        setCity(null);
        setState(null);
        setZip(null);
        setCountry(null);
        setCreated(null);
        setLastModified(null);
    }

    public void setProperties(PartnerPojo pojo) {

        setNxPartnerId(pojo.getNxPartnerId());
        setName(pojo.getName());
        setCompany(pojo.getCompanyName());
        setPartnerId(pojo.getPartnerId());
        setPartnerIdType(pojo.getPartnerIdType());
        setType(pojo.getType());
        setAddress1(pojo.getAddressLine1());
        setAddress2(pojo.getAddressLine2());
        setCity(pojo.getCity());
        setState(pojo.getState());
        setZip(pojo.getZip());
        setCountry(pojo.getCountry());
        setCreated(pojo.getCreatedDate());
        setLastModified(pojo.getModifiedDate());
    }

    public PartnerPojo getProperties(PartnerPojo pojo) {

        pojo.setNxPartnerId(getNxPartnerId());
        pojo.setCompanyName(getCompany());
        pojo.setPartnerId(getPartnerId());
        pojo.setPartnerIdType(getPartnerIdType());
        pojo.setAddressLine1(getAddress1());
        pojo.setAddressLine2(getAddress2());
        pojo.setCity(getCity());
        pojo.setState(getState());
        pojo.setZip(getZip());
        pojo.setCountry(getCountry());
        pojo.setName(getName());
        pojo.setType(getType());
        return pojo;
    }

    public void addCertificate(CertificatePropertiesForm cert) {

        if (certificates == null) {
            certificates = new ArrayList<CertificatePropertiesForm>();
        }
        certificates.add(cert);
    }

    public void addConnection(PartnerConnectionForm con) {

        if (connections == null) {
            connections = new ArrayList<PartnerConnectionForm>();
        }
        connections.add(con);
    }

    public void addChoreography(String choreographyId) {

        if (choreographies == null) {
            choreographies = new ArrayList<String>();
        }
        choreographies.add(choreographyId);
    }

    public String getAddress1() {

        return address1;
    }

    public void setAddress1(String address1) {

        this.address1 = address1;
    }

    public String getAddress2() {

        return address2;
    }

    public void setAddress2(String address2) {

        this.address2 = address2;
    }

    public Collection<CertificatePropertiesForm> getCertificates() {

        return certificates;
    }

    public void setCertificates(Collection<CertificatePropertiesForm> certificates) {

        this.certificates = certificates;
    }

    public Collection<String> getChoreographies() {

        return choreographies;
    }

    public void setChoreographies(Collection<String> choreographies) {

        this.choreographies = choreographies;
    }

    public String getCity() {

        return city;
    }

    public void setCity(String city) {

        this.city = city;
    }

    public String getCompany() {

        return company;
    }

    public void setCompany(String company) {

        this.company = company;
    }

    public Collection<PartnerConnectionForm> getConnections() {

        return connections;
    }

    public void setConnections(Collection<PartnerConnectionForm> connections) {

        this.connections = connections;
    }

    public Collection<PartnerPojo> getContacts() {

        return contacts;
    }

    public void setContacts(Collection<PartnerPojo> contacts) {

        this.contacts = contacts;
    }

    public String getCountry() {

        return country;
    }

    public void setCountry(String country) {

        this.country = country;
    }

    @Size(min = 1, message = "{partner.error.partnerid.required}")
    public String getPartnerId() {

        return partnerId;
    }

    public void setPartnerId(String partnerId) {

        this.partnerId = partnerId;
    }

    public String getState() {

        return state;
    }

    public void setState(String state) {

        this.state = state;
    }

    public int getType() {

        return type;
    }

    public void setType(int type) {

        this.type = type;
    }

    public String getZip() {

        return zip;
    }

    public void setZip(String zip) {

        this.zip = zip;
    }

    public Date getCreated() {

        return created;
    }

    public void setCreated(Date created) {

        this.created = created;
    }

    public Date getLastModified() {

        return lastModified;
    }

    public void setLastModified(Date lastModified) {

        this.lastModified = lastModified;
    }

    /**
     * @return the partnerIdType
     */
    public String getPartnerIdType() {

        return partnerIdType;
    }

    /**
     * @param partnerIdType
     *            the partnerIdType to set
     */
    public void setPartnerIdType(String partnerIdType) {

        this.partnerIdType = partnerIdType;
    }

    /**
     * @return the name
     */
    public String getName() {

        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {

        this.name = name;
    }

    /**
     * @return the nxPartnerId
     */
    public int getNxPartnerId() {

        return nxPartnerId;
    }

    /**
     * @param nxPartnerId
     *            the nxPartnerId to set
     */
    public void setNxPartnerId(int nxPartnerId) {

        this.nxPartnerId = nxPartnerId;
    }
}
