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
package org.nexuse2e.messaging;


/**
 * Simple enum to flag which binary encoding a message payload uses.
 * Each enum value should have a parameter giving a user friendly name for itself.
 *
 * Created 20.02.2016.
 */
public enum BinaryEncoding {

    UNSUPPORTED("Unsupported encoding"),
    BINARY("Binary"),
    BASE64("Base 64");

    private final String encoding;

    BinaryEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getName() {
        return encoding;
    }

    public static BinaryEncoding fromString(String desiredEncoding) {
        for (BinaryEncoding b : values()) {
            if (b.toString().equalsIgnoreCase(desiredEncoding)) {
                return b;
            }
        }
        return UNSUPPORTED;
    }
}
