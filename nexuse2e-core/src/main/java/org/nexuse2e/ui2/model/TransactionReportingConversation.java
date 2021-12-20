package org.nexuse2e.ui2.model;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.nexuse2e.ConversationStatus;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.LogPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.reporting.StatisticsEngineLog;
import org.nexuse2e.reporting.StatisticsMessage;
import org.nexuse2e.util.DateUtil;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class TransactionReportingConversation {
    private static final Logger LOG = LogManager.getLogger(TransactionReportingConversation.class);
    private String choreographyId;
    private String conversationId;
    private int nxConversationId;
    private String partnerId;
    private Date modifiedDate;
    private Date createdDate;
    private ConversationStatus status;
    private String currentAction;
    private Date endDate;
    private String turnAroundTime;
    private List<StatisticsMessage> messages;
    private List<StatisticsEngineLog> engineLogs;

    public TransactionReportingConversation(ConversationPojo conversation) {
        this.choreographyId = conversation.getChoreography().getName();
        this.conversationId = conversation.getConversationId();
        this.nxConversationId = conversation.getNxConversationId();
        this.partnerId = conversation.getPartner().getPartnerId();
        this.modifiedDate = conversation.getModifiedDate();
        this.createdDate = conversation.getCreatedDate();
        this.status = ConversationStatus.getByOrdinal(conversation.getStatus());
        this.currentAction = conversation.getCurrentAction().getName();
        this.endDate = conversation.getEndDate();
        if (this.endDate == null) {
            this.turnAroundTime = "Not terminated";
        } else {
            this.turnAroundTime = DateUtil.getDiffTimeRounded(this.createdDate, this.endDate);
        }
        this.messages = getStatisticsMessages(conversation.getMessages());
        this.engineLogs = getStatisticsEngineLogs(conversationId);
    }

    private List<StatisticsMessage> getStatisticsMessages(List<MessagePojo> messages) {
        List<StatisticsMessage> messageList = new LinkedList<>();
        for (MessagePojo message : messages) {
            messageList.add(new StatisticsMessage(message));
        }
        return messageList;
    }

    private List<StatisticsEngineLog> getStatisticsEngineLogs(String conversationId) {
        List<StatisticsEngineLog> logEntryList = new LinkedList<>();

        try {
            List<LogPojo> logEntries = Engine.getInstance().getTransactionService().getLogEntriesForReport(null, conversationId, null, false);

            for (LogPojo logEntry : logEntries) {
                logEntryList.add(new StatisticsEngineLog(logEntry));
            }
        } catch (NexusException e) {
            LOG.error("Could not get engine log entries for conversation " + conversationId, e);
        }

        return logEntryList;
    }

    public String getChoreographyId() {
        return choreographyId;
    }

    public void setChoreographyId(String choreographyId) {
        this.choreographyId = choreographyId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public int getNxConversationId() {
        return nxConversationId;
    }

    public void setNxConversationId(int nxConversationId) {
        this.nxConversationId = nxConversationId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public ConversationStatus getStatus() {
        return status;
    }

    public void setStatus(ConversationStatus status) {
        this.status = status;
    }

    public String getCurrentAction() {
        return currentAction;
    }

    public void setCurrentAction(String currentAction) {
        this.currentAction = currentAction;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getTurnAroundTime() {
        return turnAroundTime;
    }

    public void setTurnAroundTime(String turnAroundTime) {
        this.turnAroundTime = turnAroundTime;
    }

    public List<StatisticsMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<StatisticsMessage> messages) {
        this.messages = messages;
    }

    public List<StatisticsEngineLog> getEngineLogs() {
        return engineLogs;
    }

    public void setEngineLogs(List<StatisticsEngineLog> engineLogs) {
        this.engineLogs = engineLogs;
    }
}
