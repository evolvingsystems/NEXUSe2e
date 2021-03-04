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
package org.nexuse2e.dao;

import org.nexuse2e.pojo.PersistentPropertyPojo;

/**
 * Interface to be implemented for transactional operations on PersistenProperties.
 * Used for Hollywood priciple.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public interface PersistentPropertyUpdateCallback {

    /**
     * Callback method that will be invoked by Hollywood.
     * @param property The property. If the requested property was not found, a newly created
     * <code>PersistenPropertyPojo</code> object will be passed here.
     * @return <code>true</code> if property changes be committed afterwards, <code>false</code> if a
     * rollback shall be performed.
     */
    public abstract boolean update(PersistentPropertyPojo property); 
}
