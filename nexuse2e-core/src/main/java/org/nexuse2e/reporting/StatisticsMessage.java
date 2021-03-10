package org.nexuse2e.reporting;

import org.nexuse2e.MessageStatus;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.util.DateUtil;

import java.util.Date;

public class StatisticsMessage {

    private String messageId;
    private String choreographyId;
    private String actionId;
    private Date createdDate;
    private String createdDateString;
    private Integer type;
    private String typeName;
    private MessageStatus status;
    private String conversationId;
    private Integer nxMessageId;
    private Integer nxConversationId;
    private String partnerId;

    public StatisticsMessage(Object[] record) {
        messageId = (String) record[0];
        actionId = (String) record[1];
        choreographyId = (String) record[2];
        createdDate = (Date) record[3];
        type = (Integer) record[4];
        status = MessageStatus.getByOrdinal((int) record[5]);
        conversationId = (String) record[6];
        nxMessageId = (Integer) record[7];
        nxConversationId = (Integer) record[8];
        partnerId = (String) record[9];
    }

    public StatisticsMessage(MessagePojo message) {
        messageId = message.getMessageId();
        actionId = message.getAction().getName();
        createdDate = message.getCreatedDate();
        createdDateString = DateUtil.localTimeToTimezone(createdDate, DateUtil.getTimezone(), "yyyy-MM-dd HH:mm:ss z");
        type = message.getType();
        typeName = message.getTypeName();
        status = MessageStatus.getByOrdinal(message.getStatus());
        conversationId = message.getConversation().getConversationId();
        nxMessageId = message.getNxMessageId();
        nxConversationId = message.getConversation().getNxConversationId();
        partnerId = message.getParticipant().getPartner().getPartnerId();
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

    public String getCreatedDateString() {
        return createdDateString;
    }

    public void setCreatedDateString(String createdDateString) {
        this.createdDateString = createdDateString;
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
}
