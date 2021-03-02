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
package org.nexuse2e.messaging.ebxml.legacy;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;

/**
 * Patches outbound messages by adding CR/LF characters to the message payload. 
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class HttpMessageCrLfPatcher extends AbstractPipelet {

    private static Logger LOG = Logger.getLogger(HttpMessageCrLfPatcher.class);

    private String CRLF = "\r\n";

    /**
     * Default constructor.
     */
    public HttpMessageCrLfPatcher() {

        parameters = new HashMap<String, Object>();
        frontendPipelet = true;
    }

    
    
    @Override
    public MessageContext processMessage(MessageContext messageContext)
            throws IllegalArgumentException, IllegalStateException, NexusException {

        try {
            Object data = messageContext.getData();
            if (data instanceof byte[]) {
                byte[] payload = (byte[]) data;
                byte[] crlf = CRLF.getBytes("utf-8");
                byte[] copy = new byte[payload.length + crlf.length];
                System.arraycopy(payload, 0, copy, 0, payload.length);
                System.arraycopy(crlf, 0, copy, payload.length, crlf.length);
                messageContext.setData(copy);
                LOG.info("Appended CRLF to message data");
            } else {
                LOG.info("Skipped payload of type " + (data != null ? data.getClass().getName() : "null"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        return messageContext;
    }

}
