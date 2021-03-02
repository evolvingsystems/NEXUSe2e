/**
 *  NEXUSe2e Business Messaging Open Source
 *  Copyright 2000-2021, direkt gruppe GmbH
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation version 3 of
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
package org.nexuse2e.ui.action.user;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nexuse2e.Engine;

/**
 * This class will validate any given password with the password validation property from the beans.properties.
 * If no property is set, the constructor will set the matching pattern to match every password.
 * 
 * @author BWestphal
 *
 */

public class PasswordValidator {
	private Pattern pattern;
	private Matcher matcher;
	
	private static String PASSWORD_PATTERN; 
	
	public PasswordValidator() {
		PASSWORD_PATTERN = Engine.getInstance().getPasswordValidation();
		if ( PASSWORD_PATTERN != null) {
			pattern = Pattern.compile(PASSWORD_PATTERN);
		}	else {
				PASSWORD_PATTERN = "(.*)";
				pattern = Pattern.compile(PASSWORD_PATTERN);
			}	
	}
	
	/**
	 * Validate password with regular expression set in bean.properties
	 * @param password password for validation
	 * @return false invalid, true valid
	 */
	public boolean validate(final String password) {
			matcher = pattern.matcher(password);
			return matcher.matches();			
	}
}
