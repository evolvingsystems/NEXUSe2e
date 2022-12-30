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

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.logging.LogMessage;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HTTPIntegrationController implements Controller {

    private static Logger LOG = LogManager.getLogger(HTTPIntegrationController.class);

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String choreographyId = request.getParameter(Constants.PARAM_CHOREOGRAPY_ID);
        if ((choreographyId == null) || (choreographyId.length() == 0)) {
            choreographyId = request.getParameter(org.nexuse2e.messaging.httpplain.Constants.PARAM_CHOREOGRAPY_ID);
        }
        String conversationId = request.getParameter(Constants.PARAM_CONVERSATION_ID);
        if ((conversationId == null) || (conversationId.length() == 0)) {
            conversationId = request.getParameter(org.nexuse2e.messaging.httpplain.Constants.PARAM_CONVERSATION_ID);
        }
        String messageId = request.getParameter(Constants.PARAM_MESSAGE_ID);
        if ((messageId == null) || (messageId.length() == 0)) {
            messageId = request.getParameter(org.nexuse2e.messaging.httpplain.Constants.PARAM_MESSAGE_ID);
        }
        String actionId = request.getParameter(Constants.PARAM_ACTION_ID);
        if ((actionId == null) || (actionId.length() == 0)) {
            actionId = request.getParameter(org.nexuse2e.messaging.httpplain.Constants.PARAM_ACTION_ID);
        }
        String partnerId = request.getParameter(Constants.PARAM_PARTNER_ID);
        if ((partnerId == null) || (partnerId.length() == 0)) {
            partnerId = request.getParameter(org.nexuse2e.messaging.httpplain.Constants.PARAM_PARTNER_ID);
        }
        String content = request.getParameter(Constants.PARAM_CONTENT);
        String primaryKey = request.getParameter(Constants.PARAM_PRIMARY_KEY);

        StringBuffer contentBuffer = new StringBuffer();

        LOG.debug("Handling HTTP request from legacy system...");

        if ((content == null) && StringUtils.isEmpty(primaryKey)) {
            String line = null;
            InputStream inStream = request.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inStream));
            // System.out.println("Reading from input stream...");
            while ((line = bufferedReader.readLine()) != null) {
                // System.out.println( line + "\n" );
                contentBuffer.append(line + "\n");
            }
            // System.out.println("Reading from input stream done!");
            content = contentBuffer.toString();
        }

        if (StringUtils.isEmpty(primaryKey) && StringUtils.isEmpty(content)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Content or PrimaryKey parameter must be provided!");
            return null;
        }

        if (StringUtils.isEmpty(choreographyId)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Choreography ID parameter must be provided!");
            return null;
        }
        if (StringUtils.isEmpty(actionId)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action name parameter must be provided!");
            return null;
        }
        if (StringUtils.isEmpty(partnerId)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Partner ID parameter must be provided!");
            return null;
        }

        // check for duplicate message -- only process, if message id is yet unknown
        if (messageId != null && Engine.getInstance().getTransactionService().getMessageContext(messageId) != null) {
            LOG.info("Received duplicate message: " + messageId);
            response.sendError(HttpServletResponse.SC_OK, "Message will be ignored, because of a duplicate message id" +
                    " (" + messageId + ")");
        } else {
            NEXUSe2eInterface theNEXUSe2eInterface = Engine.getInstance().getInProcessNEXUSe2eInterface();

            try {
                if (StringUtils.isEmpty(primaryKey)) {
                    conversationId = theNEXUSe2eInterface.sendNewStringMessage(choreographyId, partnerId, actionId,
                            conversationId, messageId, content);
                } else {
                    conversationId = theNEXUSe2eInterface.triggerSendingNewMessage(choreographyId, partnerId,
                            actionId, conversationId, messageId, primaryKey);
                }
                /*
                if ( conversationId == null || conversationId.length() == 0 ) { // Create a new conversation if none
                was specified
                    conversationId = theNEXUSe2eInterface.sendNewStringMessage( choreographyId, partnerId, actionId,
                            content );
                    LOG.debug( "##--> New conversation ID ( choreography '" + choreographyId + "', conversation ID '"
                            + conversationId + "')!" );
                } else {
                     theNEXUSe2eInterface.sendStringMessage( conversationId, actionId, content );
                }
                */

                LOG.debug(new LogMessage("Message sent ( choreography '" + choreographyId + "', conversation ID '" + conversationId + "')!", conversationId, (messageId != null ? messageId : "unknown")));

                response.setStatus(HttpServletResponse.SC_OK);
            } catch (NexusException e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "NexusE2EException was thrown during " +
                        "submission of message: " + e);
            }
        }
        return null;
    }

} // HTTPIntegrationController
