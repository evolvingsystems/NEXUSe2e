package org.nexuse2e.reporting;

import java.util.Date;

public class StatisticsConversation {
    private final String choreographyId;
    private final String conversationId;
    private final int nxConversationId;
    private final String partnerId;
    private final Date modifiedDate;

    public StatisticsConversation(Object[] record) {
        this.choreographyId = (String) record[5];
        this.conversationId = (String) record[0];
        this.nxConversationId = (int) record[4];
        this.partnerId = (String) record[6];
        this.modifiedDate = (Date) record[2];
    }

    public String getChoreographyId() {
        return choreographyId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public int getNxConversationId() {
        return nxConversationId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }
}
