/**
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

import org.nexuse2e.pojo.ConversationPojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Statistics {

    private Date startDate;
    private Date endDate;
    private List<StatisticsMessage> failedMessages = new ArrayList<>();
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

    public List<StatisticsMessage> getFailedMessages() {
        return failedMessages;
    }

    public void setFailedMessages(List<StatisticsMessage> failedMessages) {
        this.failedMessages = failedMessages;
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
