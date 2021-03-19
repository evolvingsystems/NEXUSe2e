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

import org.nexuse2e.MessageStatus;
import org.nexuse2e.pojo.MessagePojo;

import java.util.Date;

public class StatisticsMessage {

    private String messageId;
    private String choreographyId;
    private String actionId;
    private Date createdDate;
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
        type = ((Number) record[4]).intValue();
        status = MessageStatus.getByOrdinal(((Number) record[5]).intValue());
        conversationId = (String) record[6];
        nxMessageId = ((Number) record[7]).intValue();
        nxConversationId = ((Number) record[8]).intValue();
        partnerId = (String) record[9];
    }

    public StatisticsMessage(MessagePojo message) {
        messageId = message.getMessageId();
        actionId = message.getAction().getName();
        createdDate = message.getCreatedDate();
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
