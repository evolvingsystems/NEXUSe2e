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
package org.nexuse2e.ui.security;

import java.util.Collection;
import java.util.Collections;

import org.nexuse2e.Engine;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.RolePojo;
import org.nexuse2e.pojo.UserPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * User service implementation.
 * 
 * @author Jonas Reese
 */
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private Engine engine;
    
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException, DataAccessException {

        EngineConfiguration engineConfig = engine.getCurrentConfiguration();
        if (engineConfig == null) {
            throw new DataRetrievalFailureException("NEXUSe2e configuration is not accessible");
        }
        
        UserPojo userInstance = engineConfig.getUserByLoginName(userName);
        if (userInstance == null) {
            throw new UsernameNotFoundException("User " + userName + " not found");
        }

        return new UserDetailsImpl(userInstance);
    }

    /**
     * Wrapper implementation for user details.
     */
    static class UserDetailsImpl implements UserDetails {

        private static final long serialVersionUID = 1L;

        private UserPojo user;
        private Collection<GrantedAuthority> authorities;

        public UserDetailsImpl(UserPojo user) {
            this.user = user;
            authorities = Collections.singletonList((GrantedAuthority) new GrantedAuthority() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String getAuthority() {
                        RolePojo role = UserDetailsImpl.this.user.getRole();
                        if (role != null) {
                            return role.getName();
                        }
                        return null;
                    }
                });
        }

        @Override
        public Collection<GrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public String getPassword() {
            return user.getPassword();
        }

        @Override
        public String getUsername() {
            return user.getLoginName();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return user.isActive();
        }
    }
}
