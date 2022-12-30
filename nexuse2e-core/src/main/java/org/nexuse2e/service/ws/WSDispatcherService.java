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
package org.nexuse2e.service.ws;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nexuse2e.BeanStatus;
import org.nexuse2e.DynamicWSDispatcherServlet;
import org.nexuse2e.Engine;
import org.nexuse2e.Layer;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.ListParameter;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.ParameterType;
import org.nexuse2e.messaging.Constants;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.service.AbstractService;
import org.nexuse2e.service.ReceiverAware;
import org.nexuse2e.transport.TransportReceiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.jws.WebService;
import jakarta.xml.ws.Endpoint;

/**
 * A service that dynamically registers web services.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class WSDispatcherService extends AbstractService implements ReceiverAware {

    private static final String URL_PARAM_NAME = "url";
    private static final String SERVICE_TYPE_PARAM_NAME = "serviceType";
    private static Logger LOG = LogManager.getLogger(WSDispatcherService.class);
    private Endpoint endpoint;
    private TransportReceiver transportReceiver;

    private static void process(TransportReceiver transportReceiver, String choreography, String action,
                                String partner, String conversationId, String messageId, String document) {

        MessageContext messageContext = new MessageContext();

        byte[] payload = (document != null ? document.getBytes() : null);

        TransportReceiver receiver = transportReceiver;
        if (receiver != null) {
            messageContext.setData(payload);
            if (LOG.isTraceEnabled()) {
                LOG.trace("Inbound message:\n" + document);
            }

            MessagePojo messagePojo = new MessagePojo();
            messagePojo.setType(Constants.INT_MESSAGE_TYPE_NORMAL);

            messageContext.setMessagePojo(messagePojo);
            messageContext.setOriginalMessagePojo(messagePojo);
            messageContext.getMessagePojo().setCustomParameters(new HashMap<String, String>());

            try {
                if (choreography != null && action != null && partner != null && conversationId != null && messageId != null) {
                    Engine.getInstance().getTransactionService().initializeMessage(messagePojo, messageId,
                            conversationId, action, partner, choreography);

                    MessagePayloadPojo messagePayloadPojo = new MessagePayloadPojo();
                    messagePayloadPojo.setMessage(messagePojo);
                    messagePayloadPojo.setContentId(Engine.getInstance().getIdGenerator(Constants.ID_GENERATOR_MESSAGE_PAYLOAD).getId());
                    messagePayloadPojo.setMimeType("text/xml");
                    messagePayloadPojo.setPayloadData(payload);
                    List<MessagePayloadPojo> messagePayloads = new ArrayList<MessagePayloadPojo>(1);
                    messagePayloads.add(messagePayloadPojo);
                    messagePojo.setMessagePayloads(messagePayloads);
                }
                receiver.processMessage(messageContext);
            } catch (NexusException nex) {
                nex.printStackTrace();
                LOG.error(nex);
            }
        }
    }

    ;

    @Override
    public void fillParameterMap(Map<String, ParameterDescriptor> parameterMap) {

        ListParameter serviceTypeDrowdown = new ListParameter();

        for (FrontendWebServiceType type : FrontendWebServiceType.values()) {
            serviceTypeDrowdown.addElement(type.getName(), type.toString());
        }
        parameterMap.put(SERVICE_TYPE_PARAM_NAME, new ParameterDescriptor(ParameterType.LIST, "Web service type",
                "The type of web service that shall be installed", serviceTypeDrowdown));
        parameterMap.put(URL_PARAM_NAME, new ParameterDescriptor(ParameterType.STRING, "Web service URL", "The last " +
                "part of the web service URL (e.g. /sendMessage)", ""));
    }

    @Override
    public Layer getActivationLayer() {

        return Layer.INBOUND_PIPELINES;
    }

    public void start() {

        if (getStatus() == BeanStatus.STARTED) {
            return;
        }
        ListParameter parameter = getParameter(SERVICE_TYPE_PARAM_NAME);
        FrontendWebServiceType wsType = null;
        if (parameter != null) {
            wsType = FrontendWebServiceType.valueOf(parameter.getSelectedValue());
        }

        String url = (String) getParameter(URL_PARAM_NAME);
        LOG.debug("Web service URL extension: " + url);

        if (wsType != null) {
            try {
                Object implementor = wsType.getSeiClass().newInstance();
                ((ReceiverAware) implementor).setTransportReceiver(transportReceiver);
                System.out.println("WSDispatcherService url: '" + url);
                endpoint = Endpoint.publish(url, implementor);
                super.start();
            } catch (Exception ex) {
                if (!StringUtils.equals(url, "/wsplain") && !StringUtils.equals(url, "/wscidx")) {
                    ex.printStackTrace();
                } else {
                    System.out.println("Suppressed Exception in WSDispatcherService for url '" + url + "' : " + ex.getMessage());
                }
                LOG.error(ex);
            }
        }
    }

    public void stop() {

        if (endpoint != null) {
            endpoint.stop();
            endpoint = null;
            if (DynamicWSDispatcherServlet.getInstance() != null) {
                DynamicWSDispatcherServlet.getInstance().reinitialize();
            }
        }
        super.stop();
    }

    public TransportReceiver getTransportReceiver() {

        return transportReceiver;
    }

    public void setTransportReceiver(TransportReceiver transportReceiver) {

        if (endpoint != null && endpoint.getImplementor() != null) {
            ((ReceiverAware) endpoint.getImplementor()).setTransportReceiver(transportReceiver);
        }
        this.transportReceiver = transportReceiver;
    }

    public enum FrontendWebServiceType {

        XML_DOCUMENT("Generic XML document (with routing information)", XmlDocumentServiceImpl.class), CIDX_DOCUMENT(
                "CIDX business document (no routing information)", CidxDocumentServiceImpl.class);

        private String name;
        private Class<?> seiClass;

        FrontendWebServiceType(String name, Class<?> seiClass) {

            this.name = name;
            this.seiClass = seiClass;
        }

        /**
         * Gets the human-readable service type name.
         * @return The name.
         */
        public String getName() {

            return name;
        }

        /**
         * Gets the Service Interface Implementation (SEI) class.
         * @return The SEI class.
         */
        public Class<?> getSeiClass() {

            return seiClass;
        }
    }

    @WebService(portName = "XmlDocumentServicePort", endpointInterface = "org.nexuse2e.service.ws.XmlDocumentService")
    public static class XmlDocumentServiceImpl implements XmlDocumentService, ReceiverAware {

        private TransportReceiver transportReceiver;

        public void processXmlDocument(String choreography, String action, String partner, String conversationId,
                                       String messageId, String xmlPayload) {

            process(transportReceiver, choreography, action, partner, conversationId, messageId, xmlPayload);
        }

        public TransportReceiver getTransportReceiver() {

            return transportReceiver;
        }

        public void setTransportReceiver(TransportReceiver transportReceiver) {

            this.transportReceiver = transportReceiver;
        }
    }

    @WebService(portName = "CidxDocumentServicePort", endpointInterface = "org.nexuse2e.service.ws.CidxDocumentService")
    public static class CidxDocumentServiceImpl implements CidxDocumentService, ReceiverAware {

        private TransportReceiver transportReceiver;

        public TransportReceiver getTransportReceiver() {

            return transportReceiver;
        }

        public void setTransportReceiver(TransportReceiver transportReceiver) {

            this.transportReceiver = transportReceiver;
        }

        public void processCidxDocument(String document) {

            process(transportReceiver, null, null, null, null, null, document);
        }
    }
}
