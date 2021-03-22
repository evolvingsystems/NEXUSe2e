/*
 *  NEXUSe2e Business Messaging Open Source
 *  Copyright 2000-2021, direkt gruppe GmbH
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation version 3 of
 *  the License.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.nexuse2e.reporting;
import org.nexuse2e.ConversationStatus;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.util.DateUtil;

import java.util.Date;

public class StatisticsConversation {
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

    public StatisticsConversation(Object[] record) {
        this.choreographyId = (String) record[5];
        this.conversationId = (String) record[0];
        this.nxConversationId = ((Number) record[4]).intValue();
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
        this.currentAction = conversation.getCurrentAction().getName();
        if(this.endDate == null) {
            this.turnAroundTime = "Not terminated";
        } else {
            this.turnAroundTime = DateUtil.getDiffTimeRounded(this.createdDate, this.endDate);
        }
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
}
