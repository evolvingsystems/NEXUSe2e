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
package org.nexuse2e.util;


import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * A thread-local bound STORAGE map for data. Usually to be used for pre-setting conversation and message ID so all NexusException instances will always have the information available during an entire pipeline's execution.
 * Created by JJerke on 02.10.2014.
 */
public class NexusThreadStorage {

    private static final ThreadLocal<Map<String, Object>> STORAGE = new ThreadLocal<Map<String, Object>>();

    public static void set(String identifier, Object data) {
        if (StringUtils.isBlank(identifier) || null == data) {
            return;
        }
        if (null == STORAGE.get()) {
            STORAGE.set(new HashMap<String, Object>());
        }
        STORAGE.get().put(identifier, data);
    }

    public static void remove(String identifier) {
        if (null == STORAGE.get() || StringUtils.isBlank(identifier)) {
            return;
        }
        STORAGE.get().remove(identifier);
        if (STORAGE.get().isEmpty()) {
            STORAGE.remove();
        }
    }

    public static Object get(String identifier) {
        if (null == STORAGE.get() || StringUtils.isBlank(identifier)) {
            return null;
        }
        return STORAGE.get().get(identifier);
    }
}
