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
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.ConfigurationAccessService;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.pojo.GrantPojo;
import org.nexuse2e.pojo.RolePojo;
import org.nexuse2e.pojo.UserPojo;
import org.nexuse2e.ui.form.RoleForm;
import org.nexuse2e.ui.form.UserForm;
import org.nexuse2e.util.PasswordUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for user maintenance stuff.
 *
 * @author Jonas Reese
 */
@Controller
public class UserController {

    @RequestMapping("/UserList.do")
    public String userList(Model model, ConfigurationAccessService engineConfiguration) {

        // get all users
        List<UserPojo> users = engineConfiguration.getUsers(Constants.COMPARATOR_USER_BY_NAME);

        model.addAttribute("collection", users);
        return "pages/user/user_list";
    }

    @RequestMapping("/RoleList.do")
    public String roleList(Model model, ConfigurationAccessService engineConfiguration) {

        // get all users
        List<RolePojo> roles = engineConfiguration.getRoles(Constants.COMPARATOR_ROLE_BY_NAME);

        model.addAttribute("collection", roles);
        return "pages/user/role_list";
    }
    
    @RequestMapping("/UserAdd.do")
    public String userAdd(Model model, UserForm userForm, ConfigurationAccessService engineConfiguration) {

        userForm.reset();
        model.addAttribute("roles", engineConfiguration.getRoles(Constants.COMPARATOR_ROLE_BY_NAME));
        
        return "pages/user/user_edit";
    
    }

    @RequestMapping("/RoleAdd.do")
    public String roleAdd(Model model, RoleForm roleForm, ConfigurationAccessService engineConfiguration) {

        roleForm.reset();
        model.addAttribute("roles", engineConfiguration.getRoles(Constants.COMPARATOR_ROLE_BY_NAME));
        
        return "pages/user/role_edit";
    
    }

    @RequestMapping("/UserEdit.do")
    public String userEdit(
            Model model, UserForm userForm, BindingResult bindingResult, ConfigurationAccessService engineConfiguration) {

        int userId = userForm.getNxUserId();
        
        // reset user form to avoid side effects
        userForm.reset();

        UserPojo user = engineConfiguration.getUserByNxUserId(userId);
        if (user != null) {
            userForm.init(user);
        } else {
            bindingResult.addError(new ObjectError("userForm", new String[] { "nexususer.error.notFound" }, null, "User not found"));
        }
        
        model.addAttribute("roles", engineConfiguration.getRoles(Constants.COMPARATOR_ROLE_BY_NAME));
        
        return "pages/user/user_edit";
    
    }
    
    @RequestMapping("/RoleEdit.do")
    public String roleEdit(
            Model model, RoleForm roleForm, BindingResult bindingResult, ConfigurationAccessService engineConfiguration) {

        int roleId = roleForm.getNxRoleId();
        
        // reset user form to avoid side effects
        roleForm.reset();

        RolePojo role = engineConfiguration.getRoleByNxRoleId(roleId);
        if (role != null) {
            roleForm.init(role);
        } else {
            bindingResult.addError(new ObjectError("roleForm", new String[] { "nexususer.error.role.notFound" }, null, "Role not found"));
        }
        
        return "pages/user/role_edit";
    
    }
    
    @RequestMapping(value = "/UserDelete.do", method = {RequestMethod.POST, RequestMethod.GET})
    public String userDelete(
            Model model, @RequestParam("nxUserId") int nxUserId, ConfigurationAccessService engineConfiguration)
                    throws NexusException {

        UserPojo user = engineConfiguration.getUserByNxUserId(nxUserId);
        if (user != null) {
            engineConfiguration.deleteUser(user);
        }
        return userList(model, engineConfiguration);
    }
    
    @RequestMapping(value = "/RoleDelete.do", method = {RequestMethod.POST, RequestMethod.GET})
    public String roleDelete(
            Model model, @RequestParam("nxRoleId") int nxRoleId, ConfigurationAccessService engineConfiguration)
                    throws NexusException {

        RolePojo role = engineConfiguration.getRoleByNxRoleId(nxRoleId);
        if (role != null) {
            engineConfiguration.deleteRole(role);
        }
        return roleList(model, engineConfiguration);
    }

    @RequestMapping(value = "/UserSave.do", method = RequestMethod.POST)
    public String userSave(
            Model model, @Valid UserForm userForm, BindingResult bindingResult,
            ConfigurationAccessService engineConfiguration, UserPojo loggedInUser)
                    throws NoSuchAlgorithmException, NexusException {

        // does user exist already?
        UserPojo user = engineConfiguration.getUserByNxUserId(userForm.getNxUserId());
        if (user == null) {
            // create new user
            user = new UserPojo();
        }
        // login name must be unique
        UserPojo userWithLogin = engineConfiguration.getUserByLoginName(userForm.getLoginName());
        if (userWithLogin != null && userWithLogin.getNxUserId() != user.getNxUserId()) {
            bindingResult.reject("nexususer.error.loginname.unique", "Login not unique");
        }

        if (!bindingResult.hasErrors()) {
            RolePojo role = engineConfiguration.getRoleByNxRoleId(userForm.getNxRoleId());
            if (role == null) {
                bindingResult.reject("nexususer.error.role.unknown", "Role not found");
            }
            user.setRole(role);
            if (!StringUtils.isEmpty(userForm.getPassword())) {
                // the from validation ensures that password is set for new users
                // encrypt password
                user.setPassword(PasswordUtil.hashPassword(userForm.getPassword()));
            }

            user.setFirstName(userForm.getFirstName());
            user.setLastName(userForm.getLastName());
            user.setMiddleName(userForm.getMiddleName());
            user.setLoginName(userForm.getLoginName());
            user.setActive(userForm.getActive());
            user.setVisible(true);
            Date now = new Date();
            if (user.getCreatedDate() == null) {
                user.setCreatedDate(now);
            }
            user.setModifiedDate(now);
            user.setModifiedNxUserId(loggedInUser.getNxUserId());

            // make changes persistent
            engineConfiguration.updateUser(user);
            // update form
            userForm.init(user);

            return userList(model, engineConfiguration);
        }

        model.addAttribute("roles", engineConfiguration.getRoles(Constants.COMPARATOR_ROLE_BY_NAME));

        return "pages/user/user_edit";
    }
    

    private static final String PARAMETER_NAME_GRANT_PREFIX = "__grant:";

    @RequestMapping(value = "/RoleSave.do", method = RequestMethod.POST)
    public String roleSave(
            Model model, @Valid RoleForm roleForm, BindingResult bindingResult,
            ConfigurationAccessService engineConfiguration, HttpServletRequest request, UserPojo loggedInUser)
                    throws NexusException {

        // does role exist already?
        RolePojo persistentRole = engineConfiguration.getRoleByNxRoleId(roleForm.getNxRoleId());
        RolePojo role = new RolePojo();
        if (persistentRole != null) {
            role.setCreatedDate(persistentRole.getCreatedDate());
            role.setDescription(persistentRole.getDescription());
            role.setGrantMap(persistentRole.getGrantMap());
            role.setGrants(persistentRole.getGrants());
            role.setModifiedDate(persistentRole.getModifiedDate());
            role.setModifiedNxUserId(persistentRole.getModifiedNxUserId());
            role.setName(persistentRole.getName());
            role.setNxId(persistentRole.getNxId());
        }
        // name must be unique
        RolePojo roleWithName = engineConfiguration.getRoleByName(roleForm.getName());
        if( roleWithName != null && roleWithName.getNxRoleId() != role.getNxRoleId() ) {
            bindingResult.reject("nexususer.error.role.name.unique", "Role name not unique");
        }
        
        // current date
        Date now = new Date();
        int modifier = loggedInUser.getNxUserId();
        
        role.setName(roleForm.getName());
        role.setDescription(roleForm.getDescription());
        // update grants
        Map<String,GrantPojo> oldGrants = new HashMap<String,GrantPojo>(role.getGrantMap());
        role.getGrantMap().clear();
        Enumeration<String> paramEnum = request.getParameterNames();
        while (paramEnum.hasMoreElements()) {
            String paramName = (String) paramEnum.nextElement();
            if (paramName != null && paramName.startsWith(PARAMETER_NAME_GRANT_PREFIX)) {
                String target = paramName.substring(PARAMETER_NAME_GRANT_PREFIX.length());
                // check if grant already exists; grants are identified by the action string only
                if (oldGrants.containsKey(target)) {
                    role.getGrantMap().put(target, oldGrants.get(target));
                } else {
                    GrantPojo newGrant = new GrantPojo(target, now, now, modifier);
                    role.getGrantMap().put(target, newGrant);
                }
            }
        }

        if (!bindingResult.hasErrors()) {
            if (role.getCreatedDate() == null) {
                role.setCreatedDate(now);
            }
            role.setModifiedDate(now);
            role.setModifiedNxUserId(modifier);
            
            // make changes persistent
            engineConfiguration.updateRole(role);
            
            return roleList(model, engineConfiguration);
        }
        
        roleForm.init(role);
        return "pages/user/role_edit";
    
    }
}
