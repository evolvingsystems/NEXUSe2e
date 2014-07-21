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
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.Size;

import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.ConnectionPojo;
import org.nexuse2e.pojo.TRPPojo;


/**
 * Form class for partner connections.
 * 
 * @author Jonas Reese
 */
public class PartnerConnectionForm implements Serializable {

    private static final long    serialVersionUID = 6252275396500984748L;

    private String               url;
    private String               description;
    private String               partnerId;
    private int                  nxPartnerId;
    private int                  nxConnectionId;

    private Set<CertificatePojo> certificates;
    private int                  nxCertificateId;
    private List<TRPPojo>        trps;
    private int                  nxTrpId;
    private int                  timeout;
    private int                  messageInterval;
    private boolean              secure;
    private boolean              reliable;
    private boolean              synchronous;
    private boolean              pickUp;
    private boolean              hold;
    private int                  synchronousTimeout;
    private int                  retries;
    private String               name;
    private String               loginName;
    private String               password;


    public void cleanSettings() {

        setUrl( null );
        setDescription( null );
        setNxCertificateId( 0 );
        setNxConnectionId( 0 );
        setNxPartnerId( 0 );
        setNxTrpId( 0 );
        setTimeout( 0 );
        setMessageInterval( 0 );
        setSecure( false );
        setReliable( false );
        setSynchronous( false );
        setPickUp( false );
        setHold( false );
        setSynchronousTimeout( 0 );
        setRetries( 0 );
        setName( null );
        setLoginName( null );
        setPassword( null );
        setCertificates( null );
        setTrps( null );
    }

    public ConnectionPojo getProperties( ConnectionPojo con ) {

        con.setUri( getUrl() );
        con.setDescription( getDescription() );
        con.setNxConnectionId( getNxConnectionId() );
        con.setTimeout( getTimeout() );
        con.setMessageInterval( getMessageInterval() );
        con.setSecure( isSecure() );
        con.setReliable( isReliable() );
        con.setSynchronous( isSynchronous() );
        con.setHold( isHold() );
        con.setPickUp( isPickUp() );
        con.setSynchronousTimeout( getSynchronousTimeout() );
        con.setRetries( getRetries() );
        con.setName( getName() );
        con.setLoginName( getLoginName() );
        con.setPassword( getPassword() );
        con.setCertificate( null );
        if (certificates == null) {
            certificates = new LinkedHashSet<CertificatePojo>();
        }
        for ( CertificatePojo certificatePojo : certificates ) {
            if ( nxCertificateId == certificatePojo.getNxCertificateId() ) {
                con.setCertificate( certificatePojo );
            }
        }
        if (trps == null) {
            trps = new ArrayList<TRPPojo>();
        }
        for ( TRPPojo trpPojo : trps ) {
            if ( nxTrpId == trpPojo.getNxTRPId() ) {
                con.setTrp( trpPojo );
            }
        }
        return con;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription( String description ) {

        this.description = description;
    }

    public String getUrl() {

        return url;
    }

    public void setUrl( String url ) {

        this.url = url;
    }

    public void setProperties( ConnectionPojo con ) {

        setUrl( con.getUri() );
        setDescription( con.getDescription() );
        setNxConnectionId( con.getNxConnectionId() );
        setNxPartnerId( con.getPartner().getNxPartnerId() );
        setNxCertificateId( ( con.getCertificate() == null ? 0 : con.getCertificate().getNxCertificateId() ) );
        setNxTrpId( con.getTrp().getNxTRPId() );
        setTimeout( con.getTimeout() );
        setMessageInterval( con.getMessageInterval() );
        setSecure( con.isSecure() );
        setReliable( con.isReliable() );
        setHold( con.isHold() );
        setPickUp( con.isPickUp() );
        setSynchronous( con.isSynchronous() );
        setSynchronousTimeout( con.getSynchronousTimeout() );
        setRetries( con.getRetries() );
        setName( con.getName() );
        setLoginName( con.getLoginName() );
        setPassword( con.getPassword() );
    }

    public String getPartnerId() {

        return partnerId;
    }

    public void setPartnerId( String partnerId ) {

        this.partnerId = partnerId;
    }

    public int getNxConnectionId() {

        return nxConnectionId;
    }

    public void setNxConnectionId( int nxConnectionId ) {

        this.nxConnectionId = nxConnectionId;
    }

    public Set<CertificatePojo> getCertificates() {

        return certificates;
    }

    public void setCertificates( Set<CertificatePojo> certificates ) {

        this.certificates = certificates;
    }

    public int getMessageInterval() {

        return messageInterval;
    }

    public void setMessageInterval( int messageInterval ) {

        this.messageInterval = messageInterval;
    }

    @Size(min = 1, message = "{connection.error.connectionname.required}")
    public String getName() {

        return name;
    }

    public void setName( String name ) {

        this.name = name;
    }

    public int getNxCertificateId() {

        return nxCertificateId;
    }

    public void setNxCertificateId( int nxCertificateId ) {

        this.nxCertificateId = nxCertificateId;
    }

    public int getNxTrpId() {

        return nxTrpId;
    }

    public void setNxTrpId( int nxTrpId ) {

        this.nxTrpId = nxTrpId;
    }

    public boolean isReliable() {

        return reliable;
    }

    public void setReliable( boolean reliable ) {

        this.reliable = reliable;
    }

    public int getRetries() {

        return retries;
    }

    public void setRetries( int retries ) {

        this.retries = retries;
    }

    public boolean isSecure() {

        return secure;
    }

    public void setSecure( boolean secure ) {

        this.secure = secure;
    }

    public boolean isSynchronous() {

        return synchronous;
    }

    public void setSynchronous( boolean synchronous ) {

        this.synchronous = synchronous;
    }

    /**
     * Gets the pickUp flag.
     * @return <code>true</code> if this connection is polled, <code>false</code> otherwise.
     */
    public boolean isPickUp() {
        return pickUp;
    }

    /**
     * Sets the pickUp flag.
     * @param pickUp If <code>true</code>, this connection is polled, otherwise not.
     */
    public void setPickUp( boolean pickUp ) {
        this.pickUp = pickUp;
    }

    /**
     * Gets the hold flag.
     * @return <code>true</code> if this connection is held on outbound traffic,
     * <code>false</code> otherwise.
     */
    public boolean isHold() {
        return hold;
    }

    /**
     * Sets the hold flag.
     * @param hold If <code>true</code>, this outbound messages are held, otherwise not.
     */
    public void setHold(boolean hold) {
        this.hold = hold;
    }

    /**
     * @return the synchronousTimeout
     */
    public int getSynchronousTimeout() {

        return synchronousTimeout;
    }

    /**
     * @param synchronousTimeout the synchronousTimeout to set
     */
    public void setSynchronousTimeout( int synchronousTimeout ) {

        this.synchronousTimeout = synchronousTimeout;
    }

    public int getTimeout() {

        return timeout;
    }

    public void setTimeout( int timeout ) {

        this.timeout = timeout;
    }

    public List<TRPPojo> getTrps() {

        return trps;
    }

    public void setTrps( List<TRPPojo> trps ) {

        this.trps = trps;
    }

    public int getNxPartnerId() {

        return nxPartnerId;
    }

    public void setNxPartnerId( int nxPartnerId ) {

        this.nxPartnerId = nxPartnerId;
    }

    public void reset() {

        secure = false;
        reliable = false;
        synchronous = false;
        pickUp = false;
        hold = false;
    }

    
    public String getLoginName() {
    
        return loginName;
    }

    
    public void setLoginName( String loginName ) {
    
        this.loginName = loginName;
    }

    
    public String getPassword() {
    
        return password;
    }

    
    public void setPassword( String password ) {
    
        this.password = password;
    }

}