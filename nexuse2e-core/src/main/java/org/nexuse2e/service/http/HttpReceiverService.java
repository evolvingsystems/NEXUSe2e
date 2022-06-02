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
package org.nexuse2e.service.http;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.util.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nexuse2e.BeanStatus;
import org.nexuse2e.ClusterException;
import org.nexuse2e.Engine;
import org.nexuse2e.Layer;
import org.nexuse2e.MessageStatus;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.NexusUUIDGenerator;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.ParameterType;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.messaging.MessageProcessor;
import org.nexuse2e.messaging.ebxml.v20.Constants;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.service.AbstractControllerService;
import org.nexuse2e.service.ReceiverAware;
import org.nexuse2e.transport.TransportReceiver;
import org.springframework.web.servlet.ModelAndView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

/**
 * A service that can be used by a <code>TransportReceiver</code> in order
 * to receive messages via HTTP.
 *
 * @author gesch, jonas.reese
 */
public class HttpReceiverService extends AbstractControllerService implements ReceiverAware, MessageProcessor {

    public static final String URL_PARAM_NAME = "logical_name";
    public static final String BASIC_AUTH_ENABLED = "basic_auth_enabled";
    public static final String BASIC_AUTH_NAME = "basic_auth_name";
    public static final String BASIC_AUTH_PASSWORD = "basic_auth_password";

    private static Logger LOG = LogManager.getLogger(HttpReceiverService.class);
    private TransportReceiver transportReceiver;

    @Override
    public void fillParameterMap(Map<String, ParameterDescriptor> parameterMap) {

        parameterMap.put(URL_PARAM_NAME, new ParameterDescriptor(ParameterType.STRING, "Logical Name", "Logical name " +
                "that is appended to the URL", "not_defined"));
        parameterMap.put(BASIC_AUTH_ENABLED, new ParameterDescriptor(ParameterType.BOOLEAN, "Basic Auth", "enable basic auth for incoming messages", Boolean.FALSE));
        parameterMap.put(BASIC_AUTH_NAME, new ParameterDescriptor(ParameterType.STRING, "Username", "basic auth user name", ""));
        parameterMap.put(BASIC_AUTH_PASSWORD, new ParameterDescriptor(ParameterType.STRING, "Password", "Basic auth password", ""));
    }

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax
     * .servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        try {
            LOG.debug("HTTPService: " + this);

            if (getStatus() != BeanStatus.STARTED) {
                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                return null;
            }

            Boolean basicAuthEnabled = getParameter(BASIC_AUTH_ENABLED);
            if(basicAuthEnabled != null && basicAuthEnabled) {
                String authHeader = request.getHeader("Authorization");
                if(StringUtils.isBlank(authHeader)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return null;
                }
                String basicAuthUsername = getParameter(BASIC_AUTH_NAME);
                String basicAuthPassword = getParameter(BASIC_AUTH_PASSWORD);
                byte[] tokenBytes = Base64.encodeBase64((basicAuthUsername+":"+basicAuthPassword).getBytes(StandardCharsets.UTF_8));
                String expected = "Basic "+new String(tokenBytes,StandardCharsets.UTF_8);
                if(!Objects.equals(expected,authHeader)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return null;
                }
            }

            MessageContext messageContext = new MessageContext();

            messageContext.setData(getContentFromRequest(request));
            if (LOG.isTraceEnabled()) {
                LOG.trace("Inbound message:\n" + new String((byte[]) messageContext.getData()));
            }

            messageContext.setMessagePojo(new MessagePojo());
            messageContext.setOriginalMessagePojo(messageContext.getMessagePojo());
            messageContext.getMessagePojo().setCustomParameters(new HashMap<String, String>());
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String key = headerNames.nextElement();
                String value = request.getHeader(key);
                messageContext.getMessagePojo().getCustomParameters().put(Constants.PARAMETER_PREFIX_HTTP + key.toLowerCase(), value);
                LOG.trace("  key: " + key + ", value: " + value);
            }

            Enumeration<String> requestParameters = request.getParameterNames();
            while (requestParameters.hasMoreElements()) {
                String key = requestParameters.nextElement();
                String value = request.getParameter(key);
                messageContext.getMessagePojo().getCustomParameters().put(Constants.PARAMETER_PREFIX_HTTP_REQUEST_PARAM + key, value);
            }

            try {
                messageContext.getMessagePojo().addCustomParameter("remoteAddr", request.getRemoteAddr());
                messageContext.getMessagePojo().addCustomParameter("remoteHost", request.getRemoteHost());
                messageContext.getMessagePojo().addCustomParameter("remotePort", "" + request.getRemotePort());
            } catch (RuntimeException e) {
                LOG.error(e);
            }

            MessageContext responseCtx = processMessage(messageContext);
            if (responseCtx != null && responseCtx.getSynchronusBackendResponse() instanceof HttpResponse) {
                HttpResponse synchronusBackendResponse = (HttpResponse) responseCtx.getSynchronusBackendResponse();
                response.setStatus(synchronusBackendResponse.getStatusCode());
                for (Entry<? extends String, ? extends String> e : synchronusBackendResponse.getHeaders().entrySet()) {
                    if (e.getKey().equalsIgnoreCase("content-type")) {
                        response.setContentType(e.getValue());
                        response.setHeader(e.getKey(), e.getValue());
                    } else if (e.getKey().equalsIgnoreCase("content-length")) {
                        try {
                            response.setContentLength(Integer.parseInt(e.getValue()));
                        } catch (NumberFormatException err) {
                            LOG.warn(new LogMessage("Could not set Content-Length header", responseCtx), err);
                        }
                    } else {
                        response.addHeader(e.getKey(), e.getValue());
                    }
                }
                response.getOutputStream().write(synchronusBackendResponse.getBody());
            } else {
                if (responseCtx != null && responseCtx.getMessagePojo().getStatus() == MessageStatus.FAILED.getOrdinal() && responseCtx.getParticipant().getConnection().isSynchronous()) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                } else {
                    response.setStatus(HttpServletResponse.SC_OK);
                }
            }
            LOG.trace(new LogMessage("Processing Done", messageContext.getMessagePojo()));

            // PrintWriter out = new PrintWriter( response.getOutputStream() );
            // out.println( "\n" );
            //out.flush();
            //out.close();
        } catch (ClusterException ce) {
            LOG.error(new LogMessage("cluster communication failed", ce), ce);
            response.setStatus(ce.getResponseCode());
        } catch (Exception e) {
            // print stack trace to console
            NexusUUIDGenerator gen = new NexusUUIDGenerator();
            String id = gen.getId();
            LOG.error(new LogMessage("processing failed (error-ref:" + id + ")", e), e);

            // prepare the response string (basically the SOAP faultString)
            createErrorResponse(request, response, "unabled to process incoming message (error-ref:" + id + ")");
        }

        return null;
    }

    /**
     * @param request
     * @return
     * @throws IOException
     */
    public byte[] getContentFromRequest(ServletRequest request) throws IOException {

        int contentLength = request.getContentLength();
        if (contentLength < 1) {
            throw new IOException("No payload in HTTP request!");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(request.getInputStream(), baos);
        return baos.toByteArray();
    }

    /**
     * @param request
     * @param response
     * @param message
     * @throws IOException
     */
    private void createErrorResponse(HttpServletRequest request, HttpServletResponse response, String message) throws IOException {

        if ((transportReceiver != null) && (transportReceiver.getKey().getCommunicationProtocolId().equalsIgnoreCase(
                "ebxml"))) {
            try {
                SOAPFactory soapFactory = SOAPFactory.newInstance();
                MessageFactory messageFactory = MessageFactory.newInstance();
                SOAPMessage soapMessage = messageFactory.createMessage();
                soapMessage.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");
                SOAPPart soapPart = soapMessage.getSOAPPart();
                SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
                soapEnvelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
                soapEnvelope.addAttribute(soapFactory.createName("xsi:schemaLocation"), "http://schemas.xmlsoap" +
                        ".org/soap/envelope/ http://www.oasis-open.org/committees/ebxml-msg/schema/envelope.xsd");

                SOAPBody soapBody = soapEnvelope.getBody();
                QName faultName = new QName(javax.xml.soap.SOAPConstants.URI_NS_SOAP_ENVELOPE, "Server");
                SOAPFault soapFault = soapBody.addFault();
                soapFault.setFaultCode(faultName);
                soapFault.setFaultString((message == null ? "" : message));
                soapMessage.saveChanges();
                soapMessage.writeTo(response.getOutputStream());
                response.setStatus(500);
                response.setContentType("text/xml");
            } catch (Exception e) {
                LOG.error("NEXUSe2e - Processing error creating SOAPFault ", e);
                response.sendError(500, "NEXUSe2e - Processing error creating SOAPFault " + e);
            }
        } else {
            // create simple output for none ebxml requests.
            response.setContentType("text/plain");
            response.setStatus(500);
            PrintWriter pw = new PrintWriter(response.getOutputStream());
            pw.write("NEXUSe2e - Processing error: " + message);
            pw.flush();

        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#getActivationTime()
     */
    @Override
    public Layer getActivationLayer() {

        return Layer.INBOUND_PIPELINES;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#teardown()
     */
    public void teardown() {

        super.teardown();

        transportReceiver = null;
    } // teardown

    public TransportReceiver getTransportReceiver() {

        return transportReceiver;
    }

    public void setTransportReceiver(TransportReceiver transportReceiver) {

        this.transportReceiver = transportReceiver;
    }

    public MessageContext processMessage(MessageContext messageContext) throws IllegalArgumentException,
            IllegalStateException, NexusException {

        MessageContext ctx = null;
        if (transportReceiver != null) {
            ctx = transportReceiver.processMessage(messageContext);
            if (transportReceiver.getStatus() != BeanStatus.ACTIVATED) {
                savePayload(messageContext);
            }
        } else {
            LOG.fatal("No TransportReceiver available for inbound message!");
            savePayload(messageContext);
        }
        return ctx;
    }

    private void savePayload(MessageContext messageContext) {

        String dir = Engine.getInstance().getNexusE2ERoot();
        if (dir == null) {
            dir = ".";
        }

        File dirFile = new File(dir);
        if (!dirFile.isDirectory()) {
            LOG.error(new LogMessage("NEXUSe2eRoot not pointing to a directory!", messageContext.getMessagePojo()));
        }
        File outputDir = new File(dirFile.getAbsolutePath() + "/inbox/notproc");

        outputDir.mkdirs();

        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String now = df.format(new Date());

        File outputFile = new File(outputDir.getAbsolutePath() + "/HttpReceiverService_" + now + ".dat");

        if (messageContext.getData() != null) {
            try {
                FileOutputStream fos = new FileOutputStream(outputFile);
                fos.write((byte[]) messageContext.getData());
                fos.flush();
                fos.close();
            } catch (Exception e) {
                LOG.error(new LogMessage("Error saving raw inbound message", messageContext, e), e);
            }
        } else {
            LOG.error(new LogMessage("No raw inbound message data found!", messageContext));
        }

    }

} // HttpReceiverService
