package org.nexuse2e.reporting;

import org.nexuse2e.pojo.ConversationPojo;

import java.util.*;

public class Statistics {

    private Date startDate;
    private Date endDate;
    private List<MessageStub> messages = new ArrayList<>();
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

    public List<MessageStub> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageStub> messages) {
        this.messages = messages;
    }

    public List<ConversationPojo> getConversations() {
        return conversations;
    }

    public void setConversations(List<ConversationPojo> conversations) {
        this.conversations = conversations;
    }
}
