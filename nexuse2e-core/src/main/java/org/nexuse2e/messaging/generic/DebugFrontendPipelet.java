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
package org.nexuse2e.messaging.generic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.ParameterType;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;

/**
 * Pipelet for debugging purposes.
 *
 * @author Jonas Reese
 */
public class DebugFrontendPipelet extends AbstractPipelet {

    public static final String TEXT_PARAM_NAME = "text";
    private static Logger LOG = LogManager.getLogger(AbstractPipelet.class);


    public DebugFrontendPipelet() {
        parameterMap.put(TEXT_PARAM_NAME, new ParameterDescriptor(ParameterType.STRING, "Output text", "The text to " +
                "display on the console when a message is processed by this pipelet", ""));
        setFrontendPipelet(true);
    }

    @Override
    public MessageContext processMessage(MessageContext messageContext) throws IllegalArgumentException,
            IllegalStateException, NexusException {
        LOG.info((String) getParameter(TEXT_PARAM_NAME));

        return messageContext;
    }
}
