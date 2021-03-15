package org.nexuse2e.reporting;
import org.nexuse2e.ConversationStatus;
import org.nexuse2e.pojo.ConversationPojo;

import java.util.Date;

public class StatisticsConversation {
    private String choreographyId;
    private String conversationId;
    private int nxConversationId;
    private String partnerId;
    private Date modifiedDate;
    private Date createdDate;
    private ConversationStatus status;

    public StatisticsConversation(Object[] record) {
        this.choreographyId = (String) record[5];
        this.conversationId = (String) record[0];
        this.nxConversationId = (int) record[4];
        this.partnerId = (String) record[6];
        this.modifiedDate = (Date) record[2];
    }

    public StatisticsConversation(ConversationPojo conversation) {
        this.choreographyId = conversation.getChoreography().getName();
        this.conversationId = conversation.getConversationId();
        this.nxConversationId = conversation.getNxConversationId();
        this.partnerId = conversation.getPartner().getPartnerId();
        this.createdDate = conversation.getCreatedDate();
        this.status = ConversationStatus.getByOrdinal(conversation.getStatus());
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
}
