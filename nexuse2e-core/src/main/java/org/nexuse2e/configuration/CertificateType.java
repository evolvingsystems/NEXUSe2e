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
/**
 * 
 */
package org.nexuse2e.configuration;

/**
 * @author JJerke
 * 
 *         This enumerates certificate types. Used to be part of <code>Constants.java</code>.
 */
public enum CertificateType {
    ALL(0), LOCAL(1), PARTNER(2), CA(3), REQUEST(4), PRIVATE_KEY(5), CACERT_METADATA(6), @Deprecated
    CERT_PART(7), STAGING(8);

    int typeOrdinal = 0;

    CertificateType(int ordinal) {
        this.typeOrdinal = ordinal;
    }

    public int getOrdinal() {
        return this.typeOrdinal;
    }

    public CertificateType getByOrdinal(int ordinal) {
        if (0 <= ordinal) {
            for (CertificateType oneType : CertificateType.values()) {
                if (oneType.getOrdinal() == ordinal) {
                    return oneType;
                }
            }
        }
        throw new IllegalArgumentException("Parameter must be the ordinal of a valid CertificateType!");
    }
}
