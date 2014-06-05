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
package org.nexuse2e.ui.controller.user;

import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.UserPojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.LoginForm;
import org.nexuse2e.util.PasswordUtil;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for user login and logout operations.
 *
 * @author Jonas Reese
 */
@Controller
public class LoginController {
    
    private static Logger LOG = Logger.getLogger(LoginController.class);
    
    @RequestMapping("/Login.do")
    public String login() {
        return "login.page";
    }

    @RequestMapping("/Logout.do")
    public String logout(HttpSession session) {

        UserPojo userInstance = (UserPojo) session.getAttribute(NexusE2EAction.ATTRIBUTE_USER);
        if (userInstance != null) {
            session.removeAttribute(NexusE2EAction.ATTRIBUTE_USER);
            session.removeAttribute("patchManagementForm"); // remove patches
        }

        return "login.page";
    }
    
    @RequestMapping("/LoginCheck.do")
    public String checkLogin(
            HttpSession session, LoginForm loginForm, BindingResult bindingResult)
                    throws NoSuchAlgorithmException {

        if (loginForm != null) {
            String user = loginForm.getUser();
            String pass = PasswordUtil.hashPassword( loginForm.getPass() );
            if ( user != null && user.length() > 0 ) {
                EngineConfiguration engineConfig = Engine.getInstance().getCurrentConfiguration();
                if (engineConfig == null) {
                    bindingResult.addError(
                            new ObjectError(
                                    "login", new String[] { "login.system.down" }, null, "system down"));
                } else {
                    UserPojo userInstance = engineConfig.getUserByLoginName( user );
                    if ( userInstance != null && userInstance.getPassword().equals( pass ) ) { // nx_user.password has a "not null" constraint
                        session.setAttribute( NexusE2EAction.ATTRIBUTE_USER, userInstance );
                        LOG.trace( "Login for \"" + user + "\" successful." );
                    } else {
                        bindingResult.addError(
                                new ObjectError(
                                        "login", new String[] { "login.credentials.wrong" }, null, "login failed"));
                        LOG.warn( "Login for \"" + user + "\" failed." );
                    }
                }
            }
        }

        if (bindingResult.hasErrors()) {
            return "login.page";
        }
        return "redirect:/NexusE2EAdmin.do";
    }
}
