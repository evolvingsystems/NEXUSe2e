package org.nexuse2e.ui2.model;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.nexuse2e.Engine;
import org.nexuse2e.MessageStatus;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.PipelineType;
import org.nexuse2e.pojo.LogPojo;
import org.nexuse2e.pojo.MessageLabelPojo;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.reporting.StatisticsEngineLog;
import org.nexuse2e.util.DateUtil;

import java.util.*;

public class TransactionReportingMessage {
    private static final Logger LOG = LogManager.getLogger(TransactionReportingMessage.class);
    private String messageId;
    private String choreographyId;
    private String actionId;
    private Date createdDate;
    private Date modifiedDate;
    private Integer type;
    private String typeName;
    private MessageStatus status;
    private String conversationId;
    private Integer nxMessageId;
    private Integer nxConversationId;
    private String partnerId;
    private String backendStatus;
    private Date endDate;
    private String turnAroundTime;
    private PipelineType direction;
    private String referencedMessageId;
    private Integer retries;
    private Date expirationDate;
    private String trp;
    private List<TransactionReportingMessagePayload> messagePayloads;
    private HashMap<String, String> messageLabels;
    private List<StatisticsEngineLog> engineLogs;

    public TransactionReportingMessage(MessagePojo message) {
        messageId = message.getMessageId();
        choreographyId = message.getConversation().getChoreography().getName();
        actionId = message.getAction().getName();
        createdDate = message.getCreatedDate();
        modifiedDate = message.getModifiedDate();
        type = message.getType();
        typeName = message.getTypeName();
        status = MessageStatus.getByOrdinal(message.getStatus());
        conversationId = message.getConversation().getConversationId();
        nxMessageId = message.getNxMessageId();
        nxConversationId = message.getConversation().getNxConversationId();
        partnerId = message.getParticipant().getPartner().getPartnerId();
        backendStatus = message.getBackendStatusName();
        endDate = message.getEndDate();
        if (endDate == null) {
            turnAroundTime = "Not terminated";
        } else {
            turnAroundTime = DateUtil.getDiffTimeRounded(createdDate, endDate);
        }
        direction = message.isOutbound() ? PipelineType.OUTBOUND : PipelineType.INBOUND;
        if (message.getReferencedMessage() == null) {
            referencedMessageId = "n/a";
        } else {
            referencedMessageId = message.getReferencedMessage().getMessageId();
        }
        retries = message.getRetries();
        expirationDate = message.getExpirationDate();
        trp = message.getTRP().getProtocol() + " / " + message.getTRP().getVersion();
        messagePayloads = getTransactionReportingMessagePayloads(message.getMessagePayloads());
        messageLabels = getMessageLabelMap(message.getMessageLabels());
        engineLogs = getStatisticsEngineLogs(conversationId, messageId);
    }

    private List<TransactionReportingMessagePayload> getTransactionReportingMessagePayloads(List<MessagePayloadPojo> messagePayloadPojos) {
        List<TransactionReportingMessagePayload> messagePayloads = new LinkedList<>();
        for (int i = 0; i < messagePayloadPojos.size(); i++) {
            MessagePayloadPojo messagePayloadPojo = messagePayloadPojos.get(i);
            messagePayloads.add(new TransactionReportingMessagePayload(messagePayloadPojo.getMimeType(), messagePayloadPojo.getContentId(), i));
        }
        return messagePayloads;
    }

    private HashMap<String, String> getMessageLabelMap(List<MessageLabelPojo> messageLabelPojos) {
        HashMap<String, String> messageLabelsMap = new HashMap<>();
        for (MessageLabelPojo messageLabel : messageLabelPojos) {
            messageLabelsMap.put(messageLabel.getLabel(), messageLabel.getValue());
        }
        return messageLabelsMap;
    }

    private List<StatisticsEngineLog> getStatisticsEngineLogs(String conversationId, String messageId) {
        List<StatisticsEngineLog> logEntryList = new LinkedList<>();

        try {
            List<LogPojo> logEntries = Engine.getInstance().getTransactionService().getLogEntriesForReport(null, conversationId, messageId, false);

            for (LogPojo logEntry : logEntries) {
                logEntryList.add(new StatisticsEngineLog(logEntry));
            }
        } catch (NexusException e) {
            LOG.error("Could not get engine log entries for message " + messageId, e);
        }

        return logEntryList;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getChoreographyId() {
        return choreographyId;
    }

    public void setChoreographyId(String choreographyId) {
        this.choreographyId = choreographyId;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public Integer getNxMessageId() {
        return nxMessageId;
    }

    public void setNxMessageId(Integer nxMessageId) {
        this.nxMessageId = nxMessageId;
    }

    public Integer getNxConversationId() {
        return nxConversationId;
    }

    public void setNxConversationId(Integer nxConversationId) {
        this.nxConversationId = nxConversationId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getBackendStatus() {
        return backendStatus;
    }

    public void setBackendStatus(String backendStatus) {
        this.backendStatus = backendStatus;
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

    public PipelineType getDirection() {
        return direction;
    }

    public void setDirection(PipelineType direction) {
        this.direction = direction;
    }

    public String getReferencedMessageId() {
        return referencedMessageId;
    }

    public void setReferencedMessageId(String referencedMessageId) {
        this.referencedMessageId = referencedMessageId;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getTrp() {
        return trp;
    }

    public void setTrp(String trp) {
        this.trp = trp;
    }

    public List<TransactionReportingMessagePayload> getMessagePayloads() {
        return messagePayloads;
    }

    public void setMessagePayloads(List<TransactionReportingMessagePayload> messagePayloads) {
        this.messagePayloads = messagePayloads;
    }

    public HashMap<String, String> getMessageLabels() {
        return messageLabels;
    }

    public void setMessageLabels(HashMap<String, String> messageLabels) {
        this.messageLabels = messageLabels;
    }

    public List<StatisticsEngineLog> getEngineLogs() {
        return engineLogs;
    }

    public void setEngineLogs(List<StatisticsEngineLog> engineLogs) {
        this.engineLogs = engineLogs;
    }
}
