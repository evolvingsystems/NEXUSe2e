/**
 * NEXUSe2e Business Messaging Open Source
 * Copyright 2007, Tamgroup and X-ioma GmbH
 * <p>
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation version 2.1 of
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
import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.ParameterType;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessageLabelPojo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * using labels to create a custom message id
 *
 * @author gesch
 */
public class ConversationIdModifyingPipelet extends AbstractPipelet {

    private static Logger LOG = Logger.getLogger(ConversationIdModifyingPipelet.class);

    public static final String DEFINITION_LIST = "definitionList";

    private List<String> segments = new ArrayList<>();

    public ConversationIdModifyingPipelet() {

        parameterMap.put(DEFINITION_LIST, new ParameterDescriptor(ParameterType.TEXT, "Label List",
                "List of labels and build-ins concatenated into the new conversation id. Be careful with special characters. (supported: ${now}, ${action}, ${choreography} )", ""));

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    @Override
    public void initialize(EngineConfiguration config) throws InstantiationException {

        String definitionList = getParameter(DEFINITION_LIST);
        if (StringUtils.isNotBlank(definitionList)) {
            String[] definitions = definitionList.split("\n");
            for (String definition : definitions) {
                if (StringUtils.isNotBlank(definition)) {
                    segments.add(definition);
                }
            }
            LOG.warn("Definition list must contain proper labels or build-ins. Ids are not modified!");
        } else {
            LOG.warn("Definition list must not be empty. Ids are not modified!");
        }

        super.initialize(config);
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    @Override
    public MessageContext processMessage(MessageContext messageContext) throws IllegalArgumentException,
            IllegalStateException, NexusException {

        StringBuilder newId = new StringBuilder();
        if (segments.size() > 0) {
            try {
                List<MessageLabelPojo> labels = messageContext.getMessagePojo().getMessageLabels();
                if (labels != null && labels.size() > 0) {
                    for (String segment : segments) {
                        String value = "";
                        if (segment.startsWith("${")) {
                            value = replaceBuildIn(segment.substring(2, segment.length() - 1),messageContext);
                        } else if (segment.startsWith("\"")) { // Spaces and other static parts
                            value = segment.substring(1, segment.length() - 1);
                        } else {
                            value = replaceLabel(segment, labels);
                        }

                        if (StringUtils.isNotBlank(value)) {
                            newId.append(value);
                        }
                    }
                    messageContext.getConversation().setConversationId(newId.toString());
                }
            } catch (Exception e) {
                LOG.error(new LogMessage("Error parsing outbound document", messageContext, e), e);
            }
        }
        return messageContext;
    }

    private String replaceLabel(String segment, List<MessageLabelPojo> labels) {

        for (MessageLabelPojo label : labels) {
            if (label.getLabel().equals(segment)) {
                return label.getValue();
            }
        }
        return null;
    }

    private String replaceBuildIn(String buildIn,MessageContext messageContext) {
        switch (buildIn) {
            case "now":
                SimpleDateFormat format = new SimpleDateFormat("ddMMyyyyHHmmssSSS");
                return format.format(new Date());
            case "action":
                return messageContext.getMessagePojo().getAction().getName();
            case "choreography":
                return messageContext.getConversation().getChoreography().getName();

            default:
                LOG.warn("'" + buildIn + "' is not a supported build-in. Please fix the configuration");
        }
        return "";
    }
}
