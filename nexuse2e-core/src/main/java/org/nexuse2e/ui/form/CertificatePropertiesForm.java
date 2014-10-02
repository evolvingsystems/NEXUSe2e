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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.X509Principal;
import org.nexuse2e.configuration.CertificateType;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.util.CertificateUtil;
import org.nexuse2e.util.EncryptionUtil;

/**
 * @author guido.esch
 */
public class CertificatePropertiesForm implements Serializable {

    private static final long serialVersionUID = 8526202214940679659L;
    private String            alias            = null;                // alias
    private String            commonName       = null;                // cn
    private String            organisation     = null;                // o
    private String            organisationUnit = null;                // ou
    private String            state            = null;                // st
    private String            location         = null;                // l 
    private String            email            = null;                // e
    private String            country          = null;                // c
    private String            domain           = null;                // dc
    private String            surname          = null;                // sn
    private String            notAfter;
    private String            notBefore;
    private boolean           valid;
    private String            timeRemaining;
    private String            fingerprint;
    private String            created;
    private String            issuer;
    private String            name;

    private X509Certificate   cert             = null;

    private int               nxCertificateId  = 0;

    public void setCertificateProperties( X509Certificate x509 ) {

        setCert( x509 );
        setPrincipal( CertificateUtil.getPrincipalFromCertificate( x509, true ) );
        setNotAfter( "" + x509.getNotAfter() );
        setNotBefore( "" + x509.getNotBefore() );
        valid = true;
        try {
            x509.checkValidity();
        } catch ( CertificateExpiredException e ) {
            valid = false;
        } catch ( CertificateNotYetValidException e ) {
            valid = false;
        }

        String remaining = CertificateUtil.getRemainingValidity( x509 );
        setTimeRemaining( remaining );

        try {
            setFingerprint( CertificateUtil.getMD5Fingerprint( x509 ) );
        } catch ( CertificateEncodingException e1 ) {
            setFingerprint( "not available" );
        }
    }
    
    public void setCertificateProperties(CertificatePojo cert) throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException, CertificateException, IOException {

        byte[] data = cert.getBinaryData();
        X509Certificate x509 = null;

        if (cert.getType() == CertificateType.PARTNER.getOrdinal()) {
            x509 = CertificateUtil.getX509Certificate(data);
        } else if (cert.getType() == CertificateType.LOCAL.getOrdinal()) {
            KeyStore jks = KeyStore.getInstance(CertificateUtil.DEFAULT_KEY_STORE, CertificateUtil.DEFAULT_JCE_PROVIDER);
            jks.load(new ByteArrayInputStream(cert.getBinaryData()), EncryptionUtil.decryptString(cert.getPassword()).toCharArray());
            if (jks != null) {

                Enumeration<String> aliases = jks.aliases();
                while (aliases.hasMoreElements()) {
                    String tempAlias = aliases.nextElement();
                    if (jks.isKeyEntry(tempAlias)) {
                        java.security.cert.Certificate[] certArray = jks.getCertificateChain(tempAlias);
                        if (certArray != null) {
                            x509 = (X509Certificate) certArray[0];
                        }
                    }
                }
            }
            if (x509 != null) {
                setCertificateProperties(x509);
            }
        }
        
        setName(cert.getName());
        setNxCertificateId(cert.getNxCertificateId());
    }
    
    
    public void setPrincipal( X509Principal principal ) {
        setCommonName( CertificateUtil.getCertificateInfo( principal, X509Name.CN ) );
        setCountry( CertificateUtil.getCertificateInfo( principal, X509Name.C ) );
        setOrganisation( CertificateUtil.getCertificateInfo( principal, X509Name.O ) );
        setOrganisationUnit( CertificateUtil.getCertificateInfo( principal, X509Name.OU ) );
        setEmail( CertificateUtil.getCertificateInfo( principal, X509Name.E ) );
        setState( CertificateUtil.getCertificateInfo( principal, X509Name.ST ) );
        setLocation( CertificateUtil.getCertificateInfo( principal, X509Name.L ) );
    }

    public String getCommonName() {

        return commonName;
    }

    public void setCommonName( String commonName ) {

        this.commonName = commonName;
    }

    public String getCountry() {

        return country;
    }

    public void setCountry( String country ) {

        this.country = country;
    }

    public String getDomain() {

        return domain;
    }

    public void setDomain( String domain ) {

        this.domain = domain;
    }

    public String getEmail() {

        return email;
    }

    public void setEmail( String email ) {

        this.email = email;
    }

    public String getLocation() {

        return location;
    }

    public void setLocation( String location ) {

        this.location = location;
    }

    public String getNotAfter() {

        return notAfter;
    }

    public void setNotAfter( String notAfter ) {

        this.notAfter = notAfter;
    }

    public String getNotBefore() {

        return notBefore;
    }

    public void setNotBefore( String notBefore ) {

        this.notBefore = notBefore;
    }

    public String getOrganisation() {

        return organisation;
    }

    public void setOrganisation( String organisation ) {

        this.organisation = organisation;
    }

    public String getOrganisationUnit() {

        return organisationUnit;
    }

    public void setOrganisationUnit( String organisationUnit ) {

        this.organisationUnit = organisationUnit;
    }

    public String getState() {

        return state;
    }

    public void setState( String state ) {

        this.state = state;
    }

    public String getSurname() {

        return surname;
    }

    public void setSurname( String surname ) {

        this.surname = surname;
    }

    public boolean isValid() {

        return valid;
    }

    public void setValid( boolean valid ) {

        this.valid = valid;
    }

    public String getTimeRemaining() {

        return timeRemaining;
    }

    public void setTimeRemaining( String timeRemaining ) {

        this.timeRemaining = timeRemaining;
    }

    public String getAlias() {

        return alias;
    }

    public void setAlias( String alias ) {

        this.alias = alias;
    }

    public String getFingerprint() {

        return fingerprint;
    }

    public void setFingerprint( String fingerprint ) {

        this.fingerprint = fingerprint;
    }

    public String getCreated() {

        return created;
    }

    public void setCreated( String created ) {

        this.created = created;
    }

    public String getIssuer() {

        return issuer;
    }

    public void setIssuer( String issuerCN ) {

        this.issuer = issuerCN;
    }

    public int getNxCertificateId() {

        return nxCertificateId;
    }

    public void setNxCertificateId( int nxCertificateId ) {

        this.nxCertificateId = nxCertificateId;
    }

    
    public X509Certificate getCert() {
    
        return cert;
    }


    public void setCert( X509Certificate cert ) {
    
        this.cert = cert;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
