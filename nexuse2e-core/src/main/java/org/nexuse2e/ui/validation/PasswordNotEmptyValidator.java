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

import org.apache.commons.lang.StringUtils;

/**
 * Constraint validator implementation for "password not empty" constraint.
 * 
 * @author Jonas Reese
 */
public class PasswordNotEmptyValidator implements ConstraintValidator<PasswordNotEmpty, RepeatedPassword> {

	@Override
	public void initialize(PasswordNotEmpty constraintAnnotation) {
	}

	@Override
	public boolean isValid(RepeatedPassword value, ConstraintValidatorContext context) {
		if (value.isPasswordSet() && StringUtils.isEmpty(value.getPassword()) && StringUtils.isEmpty(value.getPasswordRepeat())) {
			return true;
		}
		return (StringUtils.isNotBlank(value.getPassword()) &&
				StringUtils.isNotBlank(value.getPasswordRepeat()));
	}
}
