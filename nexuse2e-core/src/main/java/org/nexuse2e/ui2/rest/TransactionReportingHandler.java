package org.nexuse2e.ui2.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.nexuse2e.ConversationStatus;
import org.nexuse2e.Engine;
import org.nexuse2e.MessageStatus;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.dao.TransactionDAO;
import org.nexuse2e.messaging.Constants;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.pojo.PartnerPojo;
import org.nexuse2e.reporting.StatisticsConversation;
import org.nexuse2e.reporting.StatisticsMessage;
import org.nexuse2e.util.DateWithTimezoneSerializer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class TransactionReportingHandler implements Handler {
    @Override
    public boolean canHandle(String path, String method) {
        return ("GET".equalsIgnoreCase(method) && "/messages".equalsIgnoreCase(path)) ||
                ("GET".equalsIgnoreCase(method) && "/messages/count".equalsIgnoreCase(path)) ||
                ("GET".equalsIgnoreCase(method) && "/conversations".equalsIgnoreCase(path)) ||
                ("GET".equalsIgnoreCase(method) && "/conversations/count".equalsIgnoreCase(path));
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = request.getPathInfo();
        if (path != null) {
            switch (StringUtils.lowerCase(path)) {
                case "/messages":
                    this.handleMessageRequest(request, response, false);
                    break;
                case "/messages/count":
                    this.handleMessageRequest(request, response, true);
                    break;
                case "/conversations":
                    this.handleConversationRequest(request, response, false);
                    break;
                case "/conversations/count":
                    this.handleConversationRequest(request, response, true);
                    break;
            }
        }
    }

    private int getNxParticipantId(String participantId) throws NexusException {
        if (participantId == null) {
            return 0;
        }
        EngineConfiguration engineConfiguration = Engine.getInstance().getCurrentConfiguration();
        PartnerPojo partner;
        partner = engineConfiguration.getPartnerByPartnerId(participantId);
        return partner.getNxPartnerId();
    }

    private String getMessageStatusNumberFromName(String statusName) {
        if (statusName == null) {
            return null;
        }
        return String.valueOf(MessageStatus.valueOf(statusName).getOrdinal());
    }

    private String getConversationStatusNumberFromName(String statusName) {
        if (statusName == null) {
            return null;
        }
        return String.valueOf(ConversationStatus.valueOf(statusName).getOrdinal());
    }

    private Integer getMessageTypeFromName(String typeName) {
        if (typeName == null) {
            return null;
        }
        switch (typeName) {
            case "ACKNOWLEDGEMENT":
                return Constants.INT_MESSAGE_TYPE_ACK;
            case "NORMAL":
                return Constants.INT_MESSAGE_TYPE_NORMAL;
            case "ERROR":
                return Constants.INT_MESSAGE_TYPE_ERROR;
            default:
                throw new IllegalArgumentException("Message type " + typeName + " could not be resolved.");
        }
    }

    private Date getDateFromString(String dateString) throws ParseException {
        if (dateString == null) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return formatter.parse(dateString);
    }

    private void handleMessageRequest(HttpServletRequest request, HttpServletResponse response, boolean count) throws Exception {
        String pageIndex = request.getParameter("pageIndex");
        String itemsPerPage = request.getParameter("itemsPerPage");
        String conversationId = request.getParameter("conversationId");
        String messageId = request.getParameter("messageId");
        String status;
        Integer messageType;
        int participantId;
        Date startDate;
        Date endDate;

        try {
            status = getMessageStatusNumberFromName(request.getParameter("status"));
            messageType = getMessageTypeFromName(request.getParameter("type"));
            participantId = getNxParticipantId(request.getParameter("participantId"));
            startDate = getDateFromString(request.getParameter("startDate"));
            endDate = getDateFromString(request.getParameter("endDate"));
        } catch (Exception e) {
            if (count) {
                response.getOutputStream().print(new Gson().toJson(0));
            } else {
                response.getOutputStream().print(new Gson().toJson(new int[]{}));
            }
            return;
        }

        if (count) {
            returnMessageCount(response, conversationId, messageId, status, messageType, participantId, startDate, endDate);
        } else {
            returnMessages(response, pageIndex, itemsPerPage, conversationId, messageId, status, messageType, participantId, startDate, endDate);
        }
    }

    private void returnMessages(HttpServletResponse response, String pageIndex, String itemsPerPage, String conversationId, String messageId, String status, Integer messageType, int participantId, Date startDate, Date endDate) throws NexusException, IOException {
        if (NumberUtils.isNumber(pageIndex) && NumberUtils.isNumber(itemsPerPage)) {
            List<MessagePojo> reportMessages = Engine.getInstance().getTransactionService().getMessagesForReport(
                    status, 0,
                    participantId,
                    conversationId,
                    messageId,
                    messageType,
                    startDate,
                    endDate,
                    Integer.parseInt(itemsPerPage),
                    Integer.parseInt(pageIndex),
                    TransactionDAO.SORT_CREATED,
                    false);

            List<StatisticsMessage> messages = new LinkedList<>();
            for (MessagePojo messagePojo : reportMessages) {
                messages.add(new StatisticsMessage(messagePojo));
            }

            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateWithTimezoneSerializer()).create();
            String messageJson = gson.toJson(messages);
            response.getOutputStream().print(messageJson);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Required parameters: pageIndex and itemsPerPage");
        }
    }

    private void returnMessageCount(HttpServletResponse response, String conversationId, String messageId, String status, Integer messageType, int participantId, Date startDate, Date endDate) throws NexusException, IOException {
        long messagesCount = Engine.getInstance().getTransactionService().getMessagesCount(
                status,
                messageType,
                0, participantId,
                conversationId, messageId,
                startDate,
                endDate);
        String message = new Gson().toJson(messagesCount);
        response.getOutputStream().print(message);
    }

    private void handleConversationRequest(HttpServletRequest request, HttpServletResponse response, boolean count) throws Exception {
        String pageIndex = request.getParameter("pageIndex");
        String itemsPerPage = request.getParameter("itemsPerPage");
        String conversationId = request.getParameter("conversationId");
        String status;
        int participantId;
        Date startDate;
        Date endDate;

        try {
            status = getConversationStatusNumberFromName(request.getParameter("status"));
            participantId = getNxParticipantId(request.getParameter("participantId"));
            startDate = getDateFromString(request.getParameter("startDate"));
            endDate = getDateFromString(request.getParameter("endDate"));
        } catch (Exception e) {
            if (count) {
                response.getOutputStream().print(new Gson().toJson(0));
            } else {
                response.getOutputStream().print(new Gson().toJson(new int[]{}));
            }
            return;
        }

        if (count) {
            returnConversationCount(response, conversationId, status, participantId, startDate, endDate);
        } else {
            returnConversations(response, conversationId, status, participantId, startDate, endDate, pageIndex, itemsPerPage);
        }
    }

    private void returnConversations(HttpServletResponse response, String conversationId, String status, int participantId, Date startDate, Date endDate, String pageIndex, String itemsPerPage) throws NexusException, IOException {
        if (NumberUtils.isNumber(pageIndex) && NumberUtils.isNumber(itemsPerPage)) {
            List<ConversationPojo> reportConversations = Engine.getInstance().getTransactionService().getConversationsForReport(
                    status,
                    0, participantId, conversationId,
                    startDate,
                    endDate,
                    Integer.parseInt(itemsPerPage),
                    Integer.parseInt(pageIndex),
                    TransactionDAO.SORT_CREATED,
                    false);
            List<StatisticsConversation> conversations = new LinkedList<>();
            for (ConversationPojo conversationPojo : reportConversations) {
                conversations.add(new StatisticsConversation(conversationPojo));
            }

            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateWithTimezoneSerializer()).create();
            String conversationJson = gson.toJson(conversations);
            response.getOutputStream().print(conversationJson);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Required parameters: pageIndex and itemsPerPage");
        }
    }

    private void returnConversationCount(HttpServletResponse response, String conversationId, String status, int participantId, Date startDate, Date endDate) throws NexusException, IOException {
        long conversationsCount = Engine.getInstance().getTransactionService().getConversationsCount(
                status,
                0, participantId, conversationId,
                startDate,
                endDate,
                0, false);
        String message = new Gson().toJson(conversationsCount);
        response.getOutputStream().print(message);
    }
}
