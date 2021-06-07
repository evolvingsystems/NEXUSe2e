package org.nexuse2e.ui2.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.nexuse2e.ConversationStatus;
import org.nexuse2e.Engine;
import org.nexuse2e.MessageStatus;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.dao.LogDAO;
import org.nexuse2e.dao.TransactionDAO;
import org.nexuse2e.messaging.Constants;
import org.nexuse2e.pojo.*;
import org.nexuse2e.reporting.Statistics;
import org.nexuse2e.reporting.*;
import org.nexuse2e.ui2.model.TransactionReportingConversation;
import org.nexuse2e.ui2.model.TransactionReportingMessage;
import org.nexuse2e.util.DateUtil;
import org.nexuse2e.util.DateWithTimezoneSerializer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.nexuse2e.util.DateUtil.getCurrentDateMinusWeeks;

public class TransactionReportingHandler implements Handler {
    private int transactionActivityTimeframeInWeeks = 2;

    @Override
    public boolean canHandle(String path, String method) {
        return ("GET".equalsIgnoreCase(method) && "/messages".equalsIgnoreCase(path)) ||
                ("GET".equalsIgnoreCase(method) && "/messages/count".equalsIgnoreCase(path)) ||
                ("GET".equalsIgnoreCase(method) && "/conversations".equalsIgnoreCase(path)) ||
                ("GET".equalsIgnoreCase(method) && "/conversations/count".equalsIgnoreCase(path)) ||
                ("GET".equalsIgnoreCase(method) && "/participants/ids".equalsIgnoreCase(path)) ||
                ("GET".equalsIgnoreCase(method) && "/choreographies/ids".equalsIgnoreCase(path)) ||
                ("GET".equalsIgnoreCase(method) && "/engine-logs".equalsIgnoreCase(path)) ||
                ("GET".equalsIgnoreCase(method) && "/engine-logs/count".equalsIgnoreCase(path)) ||
                ("GET".equalsIgnoreCase(method) && "/conversation".equalsIgnoreCase(path)) ||
                ("GET".equalsIgnoreCase(method) && "/message".equalsIgnoreCase(path)) ||
                ("GET".equalsIgnoreCase(method) && "/choreographies".equalsIgnoreCase(path)) ||
                ("GET".equalsIgnoreCase(method) && "/partners".equalsIgnoreCase(path)) ||
                ("GET".equalsIgnoreCase(method) && "/conversation-status-counts".equalsIgnoreCase(path)) ||
                ("GET".equalsIgnoreCase(method) && "/engine-time-variables".equalsIgnoreCase(path)) ||
                ("GET".equalsIgnoreCase(method) && "/messages-failed".equalsIgnoreCase(path));
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
                case "/participants/ids":
                    this.returnParticipantIds(response);
                    break;
                case "/choreographies/ids":
                    this.returnChoreographyIds(response);
                    break;
                case "/engine-logs":
                    this.handleEngineLogRequest(request, response, false);
                    break;
                case "/engine-logs/count":
                    this.handleEngineLogRequest(request, response, true);
                    break;
                case "/conversation":
                    this.returnConversationByNxId(request, response);
                    break;
                case "/message":
                    this.returnMessageByNxId(request, response);
                    break;
                case "/choreographies":
                    this.returnStatisticsChoreographies(response);
                    break;
                case "/partners":
                    this.returnStatisticsPartners(response);
                    break;
                case "/conversation-status-counts":
                    this.returnConversationStatusCounts(response);
                    break;
                case "/engine-time-variables":
                    this.returnEngineTimeVariables(response);
                    break;
                case "/messages-failed":
                    this.returnFailedMessages(response);
                    break;
            }
        }
    }

    private int getNxParticipantId(String participantId) throws NexusException {
        if (participantId == null) {
            return 0;
        }
        EngineConfiguration engineConfiguration = Engine.getInstance().getCurrentConfiguration();
        PartnerPojo partner = engineConfiguration.getPartnerByPartnerId(participantId);
        return partner.getNxPartnerId();
    }

    private int getNxChoreographyId(String choreographyId) throws NexusException {
        if (choreographyId == null) {
            return 0;
        }
        EngineConfiguration engineConfiguration = Engine.getInstance().getCurrentConfiguration();
        ChoreographyPojo choreography = engineConfiguration.getChoreographyByChoreographyId(choreographyId);
        return choreography.getNxChoreographyId();
    }

    private String getMessageStatusNumberFromName(String statusName) {
        if (statusName == null) {
            return null;
        }
        return String.valueOf(MessageStatus.valueOf(statusName.toUpperCase()).getOrdinal());
    }

    private String getConversationStatusNumberFromName(String statusName) {
        if (statusName == null) {
            return null;
        }
        return String.valueOf(ConversationStatus.valueOf(statusName.toUpperCase()).getOrdinal());
    }

    private Integer getMessageTypeFromName(String typeName) {
        if (typeName == null) {
            return null;
        }
        switch (typeName.toUpperCase()) {
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

    private String getSeverityLevelFromName(String severity) {
        if (severity == null) {
            return null;
        }
        switch (severity) {
            case "TRACE":
                return "5000";
            case "DEBUG":
                return "10000";
            case "INFO":
                return "20000";
            case "WARN":
                return "30000";
            case "ERROR":
                return "40000";
            case "FATAL":
                return "50000";
            default:
                throw new IllegalArgumentException("Severity " + severity + " could not be resolved.");
        }
    }

    private Date getDateFromString(String dateString) throws ParseException {
        if (dateString == null) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return formatter.parse(dateString);
    }

    private String wrapWithWildcards(String input) {
        if (StringUtils.isBlank(input)) {
            return null;
        }
        return "%" + input + "%";
    }

    private void handleMessageRequest(HttpServletRequest request, HttpServletResponse response, boolean count) throws Exception {
        String pageIndex = request.getParameter("pageIndex");
        String itemsPerPage = request.getParameter("itemsPerPage");
        String conversationId = wrapWithWildcards(request.getParameter("conversationId"));
        String messageId = wrapWithWildcards(request.getParameter("messageId"));
        String status;
        Integer messageType;
        int nxParticipantId;
        int nxChoreographyId;
        Date startDate;
        Date endDate;

        try {
            status = getMessageStatusNumberFromName(request.getParameter("status"));
            messageType = getMessageTypeFromName(request.getParameter("type"));
            nxParticipantId = getNxParticipantId(request.getParameter("participantId"));
            nxChoreographyId = getNxChoreographyId(request.getParameter("choreographyId"));
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
            returnMessageCount(response, conversationId, messageId, status, messageType, nxChoreographyId, nxParticipantId, startDate, endDate);
        } else {
            returnMessages(response, pageIndex, itemsPerPage, conversationId, messageId, status, messageType, nxChoreographyId, nxParticipantId, startDate, endDate);
        }
    }

    private void returnMessages(HttpServletResponse response, String pageIndex, String itemsPerPage, String conversationId, String messageId, String status, Integer messageType, int nxChoreographyId, int nxParticipantId, Date startDate, Date endDate) throws NexusException, IOException {
        if (NumberUtils.isNumber(pageIndex) && NumberUtils.isNumber(itemsPerPage)) {
            List<MessagePojo> reportMessages = Engine.getInstance().getTransactionService().getMessagesForReport(
                    status, nxChoreographyId,
                    nxParticipantId,
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

    private void returnMessageByNxId(HttpServletRequest request, HttpServletResponse response) throws NexusException, IOException {
        String nxMessageId = request.getParameter("nxMessageId");
        if (StringUtils.isNumeric(nxMessageId)) {
            MessagePojo messagePojo = Engine.getInstance().getTransactionService().getMessage(Integer.parseInt(nxMessageId));

            TransactionReportingMessage message = new TransactionReportingMessage(messagePojo);

            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateWithTimezoneSerializer()).create();
            String messageJson = gson.toJson(message);
            response.getOutputStream().print(messageJson);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error while getting message by nxMessageId, nxMessageId is not numeric: " + nxMessageId);
        }
    }

    private void returnMessageCount(HttpServletResponse response, String conversationId, String messageId, String status, Integer messageType, int nxChoreographyId, int nxParticipantId, Date startDate, Date endDate) throws NexusException, IOException {
        long messagesCount = Engine.getInstance().getTransactionService().getMessagesCount(
                status,
                messageType,
                nxChoreographyId, nxParticipantId,
                conversationId, messageId,
                startDate,
                endDate);
        String message = new Gson().toJson(messagesCount);
        response.getOutputStream().print(message);
    }

    private void handleConversationRequest(HttpServletRequest request, HttpServletResponse response, boolean count) throws Exception {
        String pageIndex = request.getParameter("pageIndex");
        String itemsPerPage = request.getParameter("itemsPerPage");
        String conversationId = wrapWithWildcards(request.getParameter("conversationId"));
        String status;
        int nxParticipantId;
        int nxChoreographyId;
        Date startDate;
        Date endDate;

        try {
            status = getConversationStatusNumberFromName(request.getParameter("status"));
            nxParticipantId = getNxParticipantId(request.getParameter("participantId"));
            nxChoreographyId = getNxChoreographyId(request.getParameter("choreographyId"));
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
            returnConversationCount(response, conversationId, status, nxChoreographyId, nxParticipantId, startDate, endDate);
        } else {
            returnConversations(response, conversationId, status, nxChoreographyId, nxParticipantId, startDate, endDate, pageIndex, itemsPerPage);
        }
    }

    private void returnConversations(HttpServletResponse response, String conversationId, String status, int nxChoreographyId, int nxParticipantId, Date startDate, Date endDate, String pageIndex, String itemsPerPage) throws NexusException, IOException {
        if (NumberUtils.isNumber(pageIndex) && NumberUtils.isNumber(itemsPerPage)) {
            List<ConversationPojo> reportConversations = Engine.getInstance().getTransactionService().getConversationsForReport(
                    status,
                    nxChoreographyId,
                    nxParticipantId,
                    conversationId,
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

    private void returnConversationByNxId(HttpServletRequest request, HttpServletResponse response) throws NexusException, IOException {
        String nxConversationId = request.getParameter("nxConversationId");
        if (StringUtils.isNumeric(nxConversationId)) {
            ConversationPojo conversationPojo = Engine.getInstance().getTransactionService().getConversation(Integer.parseInt(nxConversationId));

            TransactionReportingConversation conversation = new TransactionReportingConversation(conversationPojo);

            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateWithTimezoneSerializer()).create();
            String conversationJson = gson.toJson(conversation);
            response.getOutputStream().print(conversationJson);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error while getting conversation by nxConversationId, nxConversationId is not numeric: " + nxConversationId);
        }
    }

    private void returnConversationCount(HttpServletResponse response, String conversationId, String status, int nxChoreographyId, int nxParticipantId, Date startDate, Date endDate) throws NexusException, IOException {
        long conversationsCount = Engine.getInstance().getTransactionService().getConversationsCount(
                status,
                nxChoreographyId,
                nxParticipantId,
                conversationId,
                startDate,
                endDate,
                0, false);
        String message = new Gson().toJson(conversationsCount);
        response.getOutputStream().print(message);
    }

    private void returnParticipantIds(HttpServletResponse response) throws NexusException, IOException {
        EngineConfiguration engineConfiguration = Engine.getInstance().getCurrentConfiguration();
        List<String> participants = new ArrayList<>();
        List<PartnerPojo> partners = engineConfiguration.getPartners(org.nexuse2e.configuration.Constants.PARTNER_TYPE_PARTNER, org.nexuse2e.configuration.Constants.PARTNERCOMPARATOR);

        for (PartnerPojo partner : partners) {
            participants.add(partner.getPartnerId());
        }

        response.getOutputStream().print(new Gson().toJson(participants));
    }

    private void returnChoreographyIds(HttpServletResponse response) throws IOException {
        EngineConfiguration engineConfiguration = Engine.getInstance().getCurrentConfiguration();
        List<String> choreographyIds = new ArrayList<>();
        List<ChoreographyPojo> choreographies = engineConfiguration.getChoreographies();

        for (ChoreographyPojo choreography : choreographies) {
            choreographyIds.add(choreography.getName());
        }

        response.getOutputStream().print(new Gson().toJson(choreographyIds));
    }

    private void handleEngineLogRequest(HttpServletRequest request, HttpServletResponse response, boolean count) throws Exception {
        String pageIndex = request.getParameter("pageIndex");
        String itemsPerPage = request.getParameter("itemsPerPage");
        String messageText = request.getParameter("messageText");
        String severity;
        Date startDate;
        Date endDate;

        try {
            startDate = getDateFromString(request.getParameter("startDate"));
            endDate = getDateFromString(request.getParameter("endDate"));
            severity = getSeverityLevelFromName(request.getParameter("severity"));
        } catch (Exception e) {
            if (count) {
                response.getOutputStream().print(new Gson().toJson(0));
            } else {
                response.getOutputStream().print(new Gson().toJson(new int[]{}));
            }
            return;
        }

        if (count) {
            returnEngineLogsCount(response, severity, messageText, startDate, endDate);
        } else {
            returnEngineLogs(response, severity, messageText, startDate, endDate, itemsPerPage, pageIndex);
        }
    }

    private void returnEngineLogs(HttpServletResponse response, String severity, String messageText, Date startDate, Date endDate, String itemsPerPage, String pageIndex) throws NexusException, IOException {
        if (NumberUtils.isNumber(pageIndex) && NumberUtils.isNumber(itemsPerPage)) {
            List<LogPojo> reportEngineLogEntries = Engine.getInstance().getTransactionService().getLogEntriesForReport(
                    severity,
                    messageText,
                    startDate,
                    endDate,
                    Integer.parseInt(itemsPerPage),
                    Integer.parseInt(pageIndex),
                    LogDAO.SORT_CREATED,
                    false);

            List<StatisticsEngineLog> engineLogs = new LinkedList<>();
            for (LogPojo logPojo : reportEngineLogEntries) {
                engineLogs.add(new StatisticsEngineLog(logPojo));
            }

            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateWithTimezoneSerializer()).create();
            String engineLogJson = gson.toJson(engineLogs);
            response.getOutputStream().print(engineLogJson);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Required parameters: pageIndex and itemsPerPage");
        }
    }

    private void returnEngineLogsCount(HttpServletResponse response, String severity, String messageText, Date startDate, Date endDate) throws NexusException, IOException {
        long engineLogsCount = Engine.getInstance().getTransactionService().getLogEntriesForReportCount(
                severity,
                messageText,
                startDate,
                endDate,
                0, false);
        String message = new Gson().toJson(engineLogsCount);
        response.getOutputStream().print(message);
    }

    private void returnConversationStatusCounts(HttpServletResponse response) throws NexusException, IOException {
        Statistics statistics = getStatistics();

        List<ConversationPojo> conversations = statistics.getConversations();
        LinkedHashMap<String, Integer> statusCounts = new LinkedHashMap<>();
        org.nexuse2e.integration.info.wsdl.ConversationStatus[] statuses = org.nexuse2e.integration.info.wsdl.ConversationStatus.values();

        for (int i = statuses.length - 1; i >= 0; i--) {
            statusCounts.put(statuses[i].name().toLowerCase(), 0);
        }
        for (ConversationPojo conversation : conversations) {
            String status = conversation.getStatusName().toLowerCase();
            statusCounts.put(status, statusCounts.get(status) + 1);
        }

        String statusCountsJson = new Gson().toJson(statusCounts);
        response.getOutputStream().print(statusCountsJson);
    }

    private void returnEngineTimeVariables(HttpServletResponse response) throws JSONException, IOException {
        int dashboardTimeFrameInDays = Engine.getInstance().getDashboardTimeFrameInDays();
        int transactionActivityTimeframeInWeeks = Engine.getInstance().getTransactionActivityTimeframeInWeeks();
        int idleGracePeriodInMinutes = Engine.getInstance().getIdleGracePeriodInMinutes();

        JSONObject variables = new JSONObject();
        variables.put("dashboardTimeFrameInDays", dashboardTimeFrameInDays);
        variables.put("transactionActivityTimeframeInWeeks", transactionActivityTimeframeInWeeks);
        variables.put("idleGracePeriodInMinutes", idleGracePeriodInMinutes);

        response.getOutputStream().print(String.valueOf(variables));
    }

    private boolean neverOrTooLongAgo(Date date) {
        return date == null || date.before(getCurrentDateMinusWeeks(transactionActivityTimeframeInWeeks));
    }

    private String formatTimeDifference(Date date) {
        if (neverOrTooLongAgo(date)) {
            return "no messages in " + transactionActivityTimeframeInWeeks + " weeks";
        }
        return DateUtil.getDiffTimeRounded(date, new Date()) + " ago";
    }

    private void returnStatisticsChoreographies(HttpServletResponse response) throws IOException {
        EngineConfiguration engineConfiguration = Engine.getInstance().getCurrentConfiguration();
        List<ChoreographyPojo> choreographyPojos = engineConfiguration.getChoreographies();
        TransactionDAO transactionDAO = Engine.getInstance().getTransactionService().getTransactionDao();

        List<StatisticsChoreography> choreographies = new LinkedList<>();
        for (ChoreographyPojo choreographyPojo : choreographyPojos) {
            StatisticsChoreography choreography = new StatisticsChoreography(choreographyPojo);
            MessagePojo lastMessageInbound = transactionDAO.getLastSuccessfulMessageByChoreographyAndDirection(choreographyPojo, false);
            MessagePojo lastMessageOutbound = transactionDAO.getLastSuccessfulMessageByChoreographyAndDirection(choreographyPojo, true);
            Date lastInboundMessageTime = lastMessageInbound != null ? lastMessageInbound.getCreatedDate() : null;
            Date lastOutboundMessageTime = lastMessageOutbound != null ? lastMessageOutbound.getCreatedDate() : null;
            if (!neverOrTooLongAgo(lastInboundMessageTime) || !neverOrTooLongAgo(lastOutboundMessageTime)) {
                choreography.setLastInboundTime(formatTimeDifference(lastInboundMessageTime));
                choreography.setLastOutboundTime(formatTimeDifference(lastOutboundMessageTime));
                choreographies.add(choreography);
            }
        }

        String choreographiesJson = new Gson().toJson(choreographies);
        response.getOutputStream().print(choreographiesJson);
    }

    private void returnStatisticsPartners(HttpServletResponse response) throws IOException, NexusException {
        EngineConfiguration engineConfiguration = Engine.getInstance().getCurrentConfiguration();
        List<PartnerPojo> partnerPojos = engineConfiguration.getPartners(
                org.nexuse2e.configuration.Constants.PARTNER_TYPE_PARTNER, org.nexuse2e.configuration.Constants.PARTNERCOMPARATOR);
        TransactionDAO transactionDAO = Engine.getInstance().getTransactionService().getTransactionDao();

        List<StatisticsPartner> partners = new LinkedList<>();
        for (PartnerPojo partnerPojo : partnerPojos) {
            StatisticsPartner partner = new StatisticsPartner(partnerPojo);
            MessagePojo lastMessageInbound = transactionDAO.getLastSuccessfulMessageByPartnerAndDirection(partnerPojo, false);
            MessagePojo lastMessageOutbound = transactionDAO.getLastSuccessfulMessageByPartnerAndDirection(partnerPojo, true);
            Date lastInboundMessageTime = lastMessageInbound != null ? lastMessageInbound.getCreatedDate() : null;
            Date lastOutboundMessageTime = lastMessageOutbound != null ? lastMessageOutbound.getCreatedDate() : null;
            if (!neverOrTooLongAgo(lastInboundMessageTime) || !neverOrTooLongAgo(lastOutboundMessageTime)) {
                partner.setLastInboundTime(formatTimeDifference(lastInboundMessageTime));
                partner.setLastOutboundTime(formatTimeDifference(lastOutboundMessageTime));
                partners.add(partner);
            }
        }

        String partnersJson = new Gson().toJson(partners);
        response.getOutputStream().print(partnersJson);
    }

    private void returnFailedMessages(HttpServletResponse response) throws NexusException, IOException {
        Statistics statistics = getStatistics();

        List<StatisticsMessage> messages = statistics.getFailedMessages();

        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateWithTimezoneSerializer()).create();
        String messagesJson = gson.toJson(messages);
        response.getOutputStream().print(messagesJson);
    }

    private Statistics getStatistics() throws NexusException {
        int dashboardTimeFrameInDays = Engine.getInstance().getDashboardTimeFrameInDays();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -dashboardTimeFrameInDays);
        Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
        TransactionDAO transactionDAO = Engine.getInstance().getTransactionService().getTransactionDao();
        int idleGracePeriodInMinutes = Engine.getInstance().getIdleGracePeriodInMinutes();
        return transactionDAO.getStatistics(timestamp, null, idleGracePeriodInMinutes);
    }
}
