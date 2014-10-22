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

import org.nexuse2e.ui.validation.PasswordRepeat;
import org.nexuse2e.ui.validation.RepeatedPassword;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author guido.esch
 */
@PasswordRepeat
public class ProtectedFileAccessForm implements RepeatedPassword, Serializable {

    private static final long serialVersionUID = -5681743313841088090L;

    public final static int   PEM              = 1;
    public final static int   DER              = 2;

    String                    certificatePath  = null;
    CommonsMultipartFile      certificate      = null;
    String                    password         = "nexus";
    String                    passwordRepeat   = "nexus";
    String                    alias            = null;
    int                       status           = 2;
    int                       format           = DER;
    int                       content          = 3;
    String                    id               = null;
    int                       nxCertificateId  = 0;

    public String getCertificatePath() {
        return certificatePath;
    }

    public void setCertificatePath(String certificatePath) {
        this.certificatePath = certificatePath;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getPasswordRepeat() {
        return passwordRepeat;
    }

    public void setPasswordRepeat(String passwordRepeat) {
        this.passwordRepeat = passwordRepeat;
    }

    @Override
    public boolean isPasswordSet() {
        return false;
    }
    
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public int getContent() {
        return content;
    }

    public void setContent(int content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CommonsMultipartFile getCertificate() {
        return certificate;
    }

    public void setCertificate(CommonsMultipartFile certificate) {
        this.certificate = certificate;
    }

    public int getNxCertificateId() {
        return nxCertificateId;
    }

    public void setNxCertificateId(int nxCertificateId) {
        this.nxCertificateId = nxCertificateId;
    }
}
