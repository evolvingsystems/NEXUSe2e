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

import javax.validation.constraints.Size;

import org.nexuse2e.pojo.UserPojo;
import org.nexuse2e.ui.validation.NxId;
import org.nexuse2e.ui.validation.PasswordNotEmpty;
import org.nexuse2e.ui.validation.PasswordRepeat;
import org.nexuse2e.ui.validation.RepeatedPassword;

/**
 * Form for user data.
 * @author Sebastian Schulze, Jonas Reese
 * @date 04.01.2007
 */
@PasswordRepeat
@PasswordNotEmpty
public class UserForm implements Serializable, RepeatedPassword {

    private static final long serialVersionUID = -1916148187783943985L;

    // the user instance
    private int nxUserId;
    private String password;
    private String passwordRepeat;
    private int    nxRoleId;
    private String firstName;
    private String lastName;
    private String middleName;
    private String loginName;
    private boolean active = true;

    /**
     * @param user the user to set
     */
    public void init(UserPojo user) {
        
        nxUserId = user.getNxUserId();
        password = null;
        passwordRepeat = null;
        nxRoleId = user.getRole().getNxRoleId();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        middleName = user.getMiddleName();
        loginName = user.getLoginName();
        active = user.isActive();
    }
    
    public void reset() {

        nxUserId = 0;
        password = null;
        passwordRepeat = null;
        nxRoleId = 0;
        firstName = null;
        lastName = null ;
        middleName = null;
        loginName = null;
        active = true;
    }
    
    @Size(min = 1, message = "{nexususer.error.lastname.required}")
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName( String lastName ) {
        this.lastName = lastName;
    }
    
    @Size(min = 1, message = "{nexususer.error.firstname.required}")
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName( String firstName ) {
        this.firstName = firstName;
    }
    
    public String getMiddleName() {
        return middleName;
    }
    
    public void setMiddleName( String middleName ) {
        this.middleName = middleName;
    }

    @Size(min = 1, message = "{nexususer.error.loginname.required}")
    public String getLoginName() {
        return loginName;
    }
    
    public void setLoginName( String loginName ) {
        this.loginName = loginName;
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    public void setPassword( String password ) {
        this.password = password;
    }
    
    @Override
    public String getPasswordRepeat() {
        return passwordRepeat;
    }
    
    public void setPasswordRepeat( String password ) {
        passwordRepeat = password;
    }
    
    public boolean getActive() {
        return active;
    }
    
    public void setActive( boolean active ) {
        this.active = active;
    }
    
    @NxId(message = "{nexususer.error.role.required}")
    public int getNxRoleId() {
        return nxRoleId;
    }
    
    public void setNxRoleId( int roleId ) {
        this.nxRoleId = roleId;
    }
    
    public void setNxUserId( int nxUserId ) {
        this.nxUserId = nxUserId;
    }
    
    public int getNxUserId() {
        return nxUserId;
    }

    @Override
    public boolean isPasswordSet() {
        return getNxUserId() != 0;
    }
}
