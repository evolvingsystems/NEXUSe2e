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

import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;
import org.nexuse2e.util.PasswordUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * NEXUSe2e user password encoder.
 *
 * @author Jonas Reese
 */
@Component
public class Nexuse2ePasswordEncoder implements PasswordEncoder {

    private static final Logger LOG = Logger.getLogger(Nexuse2ePasswordEncoder.class);

    @Override
    public String encode(CharSequence rawPassword) {
        if (rawPassword == null) {
            return null;
        }
        try {
            return PasswordUtil.hashPassword(rawPassword.toString());
        } catch (NoSuchAlgorithmException e) {
            LOG.error(e);
            return null;
        }
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return rawPassword != null && encodedPassword != null && encodedPassword.equals(encode(rawPassword));
    }
}
