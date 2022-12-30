/*
    You can find the API documentation in apiSpec.yaml file in
    \nexuse2e-core\src\main\javadoc\doc-files
 */

package org.nexuse2e.ui2.rest;

import com.google.gson.Gson;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.messaging.MessageHandlingCenter;
import org.nexuse2e.pojo.ConversationPojo;

import java.io.IOException;
import java.util.ArrayList;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.nexuse2e.util.FileUtil.readAll;

public class DataManipulationHandler implements Handler {
    private static final Logger LOG = LogManager.getLogger(DataManipulationHandler.class);

    @Override
    public boolean canHandle(String path, String method) {
        return ("POST".equalsIgnoreCase(method) && "/messages/requeue".equalsIgnoreCase(path)) || ("POST".equalsIgnoreCase(method) && "/messages/stop".equalsIgnoreCase(path)) || ("POST".equalsIgnoreCase(method) && "/conversations/delete".equalsIgnoreCase(path));
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = request.getPathInfo();
        if (path != null) {
            switch (StringUtils.lowerCase(path)) {
                case "/messages/requeue":
                    this.requeueMessages(request, response);
                    break;
                case "/messages/stop":
                    this.stopMessages(request, response);
                    break;
                case "/conversations/delete":
                    this.deleteConversations(request, response);
                    break;
            }
        }
    }

    private void requeueMessages(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestBody = readAll(request.getInputStream());
        String[] messageIds = new Gson().fromJson(requestBody, String[].class);
        ArrayList<String> failedMessageIds = new ArrayList<>();

        for (String messageId : messageIds) {
            try {
                MessageHandlingCenter.getInstance().requeueMessage(messageId);
            } catch (NexusException e) {
                failedMessageIds.add(messageId);
                LOG.error("An error occurred while trying to requeue message " + messageId, e);
            }
        }

        if (failedMessageIds.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred when trying to " +
                    "requeue " + failedMessageIds.size() + " / " + messageIds.length + " messages: " + failedMessageIds);
        }
    }

    private void stopMessages(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestBody = readAll(request.getInputStream());
        String[] messageIds = new Gson().fromJson(requestBody, String[].class);
        ArrayList<String> failedMessageIds = new ArrayList<>();

        for (String messageId : messageIds) {
            try {
                Engine.getInstance().getTransactionService().stopProcessingMessage(messageId);
            } catch (NexusException e) {
                failedMessageIds.add(messageId);
                LOG.error("An error occurred while trying to stop message " + messageId, e);
            }
        }

        if (failedMessageIds.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An error occurred when trying to stop " + failedMessageIds.size() + " / " + messageIds.length +
                            " messages: " + failedMessageIds);
        }
    }

    private void deleteConversations(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestBody = readAll(request.getInputStream());
        String[] conversationIds = new Gson().fromJson(requestBody, String[].class);
        ArrayList<String> failedConversationIds = new ArrayList<>();

        for (String conversationId : conversationIds) {
            try {
                ConversationPojo conversation =
                        Engine.getInstance().getTransactionService().getConversation(conversationId);
                Engine.getInstance().getTransactionService().deleteConversation(conversation);
            } catch (NexusException e) {
                failedConversationIds.add(conversationId);
                LOG.error("An error occurred while trying to delete conversation " + conversationId, e);
            }
        }

        if (failedConversationIds.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred when trying to delete" +
                    " " + failedConversationIds.size() + " / " + conversationIds.length + " conversations: " + failedConversationIds);
        }
    }
}
