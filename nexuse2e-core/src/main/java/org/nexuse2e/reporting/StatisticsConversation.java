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
        this.nxConversationId = ((Number) record[4]).intValue();
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
