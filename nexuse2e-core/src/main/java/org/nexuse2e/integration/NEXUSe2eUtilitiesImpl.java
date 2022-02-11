/**
 * NEXUSe2e Business Messaging Open Source
 * Copyright 2000-2021, direkt gruppe GmbH
 * <p>
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation version 3 of
 * the License.
 * <p>
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.nexuse2e.integration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.PartnerPojo;

/**
 * Default implementation for {@link NEXUSe2eUtilities} interface.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class NEXUSe2eUtilitiesImpl implements NEXUSe2eUtilities {

    private static Logger LOG = LogManager.getLogger(NEXUSe2eUtilitiesImpl.class);

    public boolean containsPartner(String partnerId, String choreographyName) {

        try {
            PartnerPojo partner = Engine.getInstance().getCurrentConfiguration().getPartnerByPartnerId(partnerId);
            if (partner != null) {
                if (choreographyName == null || choreographyName.trim().length() == 0) {
                    return true;
                }
                ChoreographyPojo choreography =
                        Engine.getInstance().getCurrentConfiguration().getChoreographyByChoreographyId(choreographyName);
                if (choreography != null) {
                    return (Engine.getInstance().getCurrentConfiguration().getParticipantFromChoreographyByPartner(choreography, partner) != null);

                }
            }
        } catch (NexusException e) {
            LOG.error(e);
        }
        return false;
    }

}
