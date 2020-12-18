package org.nexuse2e.reporting;

import org.nexuse2e.pojo.ConversationPojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Statistics {

    private Date startDate;
    private Date endDate;
    private List<StatisticsMessage> messages = new ArrayList<>();
    private List<StatisticsConversation> idleConversations = new ArrayList<>();
    private List<ConversationPojo> conversations = new ArrayList<>();


    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<StatisticsMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<StatisticsMessage> messages) {
        this.messages = messages;
    }

    public List<StatisticsConversation> getIdleConversations() {
        return idleConversations;
    }

    public void setIdleConversations(List<StatisticsConversation> idleConversations) {
        this.idleConversations = idleConversations;
    }

    public List<ConversationPojo> getConversations() {
        return conversations;
    }

    public void setConversations(List<ConversationPojo> conversations) {
        this.conversations = conversations;
    }
}
