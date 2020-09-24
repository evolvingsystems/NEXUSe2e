package org.nexuse2e.reporting;

import org.nexuse2e.MessageStatus;

import java.util.Date;
import java.util.List;
import org.nexuse2e.messaging.Constants;

public class MessageStub {

    private String messageId;
    private String choreographyId;
    private String actionId;
    private Date createdDate;
    private Integer type;
    private MessageStatus status;
    private String conversationId;

    public MessageStub(Object[] record) {

        messageId = (String)record[0];
        actionId = (String)record[1];
        choreographyId = (String)record[2];
        createdDate = (Date)record[3];
        type = (Integer)record[4];
        status = MessageStatus.getByOrdinal((int)record[5]);
        conversationId = (String)record[6];
    }

    public String toString() {
        return "{'messageId': '" + messageId + "'," +
                "'actionId': '" + actionId + "'," +
                "'choreographyId': '" + choreographyId + "'," +
                "'createdDate': '" + createdDate + "'," +
                "'type': " + type + "," +
                "'status': '" + status + "'," +
                "'conversationId': '" + conversationId + "'}";
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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
}
