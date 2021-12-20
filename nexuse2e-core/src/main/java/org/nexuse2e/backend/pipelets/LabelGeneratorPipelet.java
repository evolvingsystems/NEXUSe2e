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
package org.nexuse2e.backend.pipelets;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.nexuse2e.BeanStatus;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.ParameterType;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessageLabelPojo;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.util.*;

/**
 * The pipelet uses XSLT to extract String values from an XML payload and associates these Strings as labels on the message.
 * The configuration uses a String parameter that contains a name/value list of label names and XPath definitions pairs
 * to create the labels to associate.
 *
 * @author mbreilmann
 */
public class LabelGeneratorPipelet extends AbstractPipelet {

    private static Logger LOG = LogManager.getLogger(LabelGeneratorPipelet.class);

    public static final String XPATH_LIST = "xPathList";
    public static final String LABEL_PREFIX = "labelPrefix";

    private HashMap<String, String> labelDefinitions = new HashMap<String, String>();

    public LabelGeneratorPipelet() {

        parameterMap.put(XPATH_LIST, new ParameterDescriptor(ParameterType.TEXT, "Label XPath List",
                "List of labels mapped to xpath statements. One label=<xpath> statement per line. Please check w3schools for tutorials on querying xml with xpath statements.", ""));
        parameterMap.put(LABEL_PREFIX, new ParameterDescriptor(ParameterType.STRING, "Label Prefix", "Label Prefix",
                ""));
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    @Override
    public void initialize(EngineConfiguration config) throws InstantiationException {

        String xPathListValue = getParameter(XPATH_LIST);
        String prefix = getParameter(LABEL_PREFIX);
        if (StringUtils.isBlank(prefix)) {
            prefix = "";
        }
        if (StringUtils.isBlank(xPathListValue)) {

            status = BeanStatus.ERROR;
            LOG.warn("No value for setting 'xPath' provided! No labels will be generated");
            return;
        }

        LOG.trace("xPath List: " + xPathListValue);


        labelDefinitions = new HashMap<String, String>();
        String[] pairs = xPathListValue.split("\n");
        for (String pair : pairs) {
            String[] keyvalue = pair.split("=");
            if (keyvalue.length == 2) {
                labelDefinitions.put(prefix + keyvalue[0], keyvalue[1]);
            }
        }


        super.initialize(config);
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    @Override
    public MessageContext processMessage(MessageContext messageContext) throws IllegalArgumentException,
            IllegalStateException, NexusException {

        // Run XPath to identify label values
        try {

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(false);
            DocumentBuilder builder;
            builder = documentBuilderFactory.newDocumentBuilder();
            XPath xPath = XPathFactory.newInstance().newXPath();

            for (MessagePayloadPojo payload : messageContext.getMessagePojo().getMessagePayloads()) {
                if (payload.getPayloadData() != null && payload.getPayloadData().length > 0) {
                    // find labels/label values
                    Document document = builder.parse(new InputSource(new ByteArrayInputStream(payload.getPayloadData())));
                    SortedSet<String> keys = new TreeSet<String>(labelDefinitions.keySet());
                    for (Iterator<String> iter = keys.iterator(); iter.hasNext(); ) {
                        String label = (String) iter.next();
                        String xPathString = (String) labelDefinitions.get(label);
                        Node node = (Node) xPath.evaluate(xPathString, document, XPathConstants.NODE);
                        if (node != null) {
                            String value = node.getNodeValue();
                            LOG.debug(new LogMessage("Label " + label + " - found: " + value, messageContext.getMessagePojo()));
                            if (value == null) {
                                value = "n/a";
                            }
                            MessageLabelPojo messageLabelPojo = new MessageLabelPojo(messageContext.getMessagePojo(),
                                    new Date(), new Date(), 1, label, value);
                            List<MessageLabelPojo> messageLabels = messageContext.getMessagePojo().getMessageLabels();
                            if (messageLabels == null) {
                                messageLabels = new ArrayList<MessageLabelPojo>();
                                messageContext.getMessagePojo().setMessageLabels(messageLabels);
                            }
                            messageLabels.add(messageLabelPojo);
                        } else {
                            LOG.debug(new LogMessage("Label " + label + " - No match found", messageContext.getMessagePojo()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.error(new LogMessage("Error parsing outbound document", messageContext, e), e);
        }

        return messageContext;
    }
}
