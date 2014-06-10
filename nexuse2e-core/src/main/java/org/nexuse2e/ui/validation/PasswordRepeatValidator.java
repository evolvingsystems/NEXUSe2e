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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Constraint validator implementation for "repeat password correctly" constraint.
 * 
 * @author Jonas Reese
 */
public class PasswordRepeatValidator implements ConstraintValidator<PasswordRepeat, RepeatedPassword> {

	@Override
	public void initialize(PasswordRepeat constraintAnnotation) {
	}

	@Override
	public boolean isValid(RepeatedPassword value, ConstraintValidatorContext context) {
		if (value.getPassword() != null && value.getPasswordRepeat() != null) {
			return value.getPassword().equals(value.getPasswordRepeat());
		} else if (value.getPassword() == null && value.getPasswordRepeat() == null) {
			return true;
		}
		return false;
	}
}
