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
package org.nexuse2e.messaging.generic;

import org.nexuse2e.NexusException;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.util.ServerPropertiesUtil;

/**
 * This pipelet replaces the server property variables in the payload section(s)'s content IDs with the
 * variable values.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class PayloadContentIdServerPropertiesReplacePipelet extends AbstractPipelet {
    
    public PayloadContentIdServerPropertiesReplacePipelet() {
        frontendPipelet = true;
    }

    @Override
    public MessageContext processMessage( MessageContext messageContext )
            throws IllegalArgumentException, IllegalStateException, NexusException {

        for (MessagePayloadPojo payload : messageContext.getMessagePojo().getMessagePayloads()) {
            String s = payload.getContentId();
            if (s != null) {
                s = ServerPropertiesUtil.replacePayloadDependentValues( s, payload.getSequenceNumber(), messageContext, true );
            }
            payload.setContentId(s);
        }
        
        return messageContext;
    }

}
