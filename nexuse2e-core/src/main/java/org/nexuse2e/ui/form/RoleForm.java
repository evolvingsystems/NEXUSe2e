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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.Size;

import org.nexuse2e.pojo.RolePojo;
import org.nexuse2e.ui.security.AccessController.ParsedRequest;


/**
 * Form for role data.
 * @author Sebastian Schulze, Jonas Reese
 * @date 28.01.2007
 */
public class RoleForm implements Serializable {
    
    private static final long serialVersionUID = -8893742272682115403L;

    //  the user instance
    private int    nxRoleId;
    private String name;
    private String description;
    private Map<String,Set<ParsedRequest>> allowedRequests;

    /**
     * Initialize with given role.
     * @param role the role to set
     */
    public void init(RolePojo role) {
        nxRoleId = role.getNxRoleId();
        name = role.getName();
        description = role.getDescription();
        allowedRequests = role.getAllowedRequests();
    }
    
    public void reset() {
        nxRoleId = 0;
        name = null;
        description = null;
        allowedRequests = new HashMap<String,Set<ParsedRequest>>();
    }
    
    @Size(min = 1, message = "{nexususer.error.role.name.required}")
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public int getNxRoleId() {
        return nxRoleId;
    }

    public void setNxRoleId(int nxRoleId) {
        this.nxRoleId = nxRoleId;
    }

    public Map<String, Set<ParsedRequest>> getAllowedRequests() {
        return allowedRequests;
    }

    public void setAllowedRequests(Map<String, Set<ParsedRequest>> allowedRequests) {
        this.allowedRequests = allowedRequests;
    }
}
