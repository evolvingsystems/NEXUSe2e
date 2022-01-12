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
package org.nexuse2e.backend.pipelets;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.ParameterType;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;

import java.util.Collections;
import java.util.Map;

/**
 * @author jjerke
 * <p>
 * This pipelet is used to forcibly set the content type of a message being processed in the backend pipeline.
 * It does so by reading it's sole configuration parameter - the target type - and simply overwriting the
 * value in the message being processed.
 */
public class ContentTypeChangerPipelet extends AbstractOutboundBackendPipelet {

    private static final String CONTENT_TYPE_PARAM_NAME = "contentType";
    @SuppressWarnings("unused")
    private static Logger LOG = LogManager.getLogger(ContentTypeChangerPipelet.class);

    /**
     * Default constructor.
     */
    public ContentTypeChangerPipelet() {
        parameterMap.put(CONTENT_TYPE_PARAM_NAME, new ParameterDescriptor(ParameterType.STRING, "Content type", "The "
                + "desired content type. If left blank, type will not be modified.", ""));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.nexuse2e.backend.pipelets.AbstractOutboundBackendPipelet#processPayloadAvailable(org.nexuse2e
     * .messaging.MessageContext)
     */
    @Override
    public MessageContext processPayloadAvailable(MessageContext messageContext) throws NexusException {

        String targetContentType = getParameter(CONTENT_TYPE_PARAM_NAME);

        if (StringUtils.isBlank(targetContentType)) {
            return messageContext;
        }

        // Iterate all payloads and set mime-type to the given value
        if (null != messageContext.getMessagePojo() && null != messageContext.getMessagePojo().getMessagePayloads()) {
            for (MessagePayloadPojo onePayload : messageContext.getMessagePojo().getMessagePayloads()) {
                onePayload.setMimeType(targetContentType);
            }
        }

        return messageContext;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.nexuse2e.backend.pipelets.AbstractOutboundBackendPipelet#processPrimaryKeyAvailable(org.nexuse2e
     * .messaging.MessageContext)
     */
    @Override
    public MessageContext processPrimaryKeyAvailable(MessageContext messageContext) throws NexusException {

        return messageContext;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.nexuse2e.Configurable#getParameterMap()
     */
    public Map<String, ParameterDescriptor> getParameterMap() {

        return Collections.unmodifiableMap(parameterMap);
    }

}
