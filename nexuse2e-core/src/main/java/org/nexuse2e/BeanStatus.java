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
package org.nexuse2e;

/**
 * The possible states of a bean that implements the <code>Manageable</code> interface.
 * @see org.nexuse2e.Manageable
 */
public enum BeanStatus {
    ERROR(-1), UNDEFINED(0), INSTANTIATED(1), INITIALIZED(2), ACTIVATED(3), STARTED(4);

    private int value;

    BeanStatus( int value ) {

        this.value = value;
    }

    public int getValue() {

        return value;
    }
}