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
package org.nexuse2e.ui.validation;

/**
 * This interface shall be implemented by domain classes that keep a password and a password repetition.
 * 
 * @author Jonas Reese
 */
public interface RepeatedPassword {

	/**
	 * Gets the plain-text password if set, or <code>null</code>.
	 * @return The plain-text password.
	 */
	public String getPassword();

	/**
	 * Gets the plain-text repeated password if set, or <code>null</code>.
	 * @return The plain-text repeated password.
	 */
	public String getPasswordRepeat();
	
	/**
	 * Determines if a password is already set. This allows to set a blank password in order
	 * avoid having to specify a password every time the user is edited.
	 * @return
	 */
	public boolean isPasswordSet();
}
