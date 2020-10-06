/**
 *  NEXUSe2e Business Messaging Open Source
 *  Copyright 2000-2009, Tamgroup and X-ioma GmbH
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation version 2.1 of
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
package org.nexuse2e.ui.action.reporting;

import org.apache.commons.lang.time.DateUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.PartnerPojo;
import org.nexuse2e.reporting.MessageStub;
import org.nexuse2e.reporting.Statistics;
import org.nexuse2e.ui.action.NexusE2EAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Fills the context for the statistics report(s).
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class ReportingStatisticsAction extends NexusE2EAction {

    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping,
            ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response,
            EngineConfiguration engineConfiguration,
            ActionMessages errors,
            ActionMessages messages ) throws Exception {

        Calendar cal = Calendar.getInstance();
        cal.add( Calendar.DATE, -1 );
        Timestamp timestamp = new Timestamp( cal.getTimeInMillis() );
        Statistics statistics = Engine.getInstance().getTransactionService().getTransactionDao().getStatistics(timestamp, null);

        List<MessageStub> messageStubs = statistics.getMessages();
        request.setAttribute("messages", messageStubs);

        Map<String, Integer> messageStatusCounts = new HashMap<>();
        for (MessageStub message : statistics.getMessages()) {
            String status = message.getStatus().toString().toLowerCase();
            Integer count = messageStatusCounts.get(status);
            if(count == null) {
                messageStatusCounts.put(status, 1);
            } else {
                messageStatusCounts.put(status, ++count);
            }
        }
        request.setAttribute("messageStatusCounts", toJson(messageStatusCounts));

        List<ConversationPojo> conversations = statistics.getConversations();
        Map<String, Integer> conversationStatusCounts = new HashMap<>();
        for (ConversationPojo conversation : conversations) {
            String status = conversation.getStatusName().toLowerCase();
            Integer count = conversationStatusCounts.get(status);
            if (count == null) {
                conversationStatusCounts.put(status, 1);
            } else {
                conversationStatusCounts.put(status, ++count);
            }
        }
        request.setAttribute("conversationStatusCounts", toJson(conversationStatusCounts));

        Map<Date, Integer> timeCounts = new HashMap<>();
        for (MessageStub message : statistics.getMessages()) {
            Date truncated = DateUtils.truncate(message.getCreatedDate(), Calendar.HOUR);
            Integer count = timeCounts.get(truncated);
            if(count == null) {
                timeCounts.put(truncated, 1);
            } else {
                timeCounts.put(truncated, ++count);
            }
        }
        request.setAttribute("timeCounts", toJson(timeCounts));

        List<PartnerPojo> partners = engineConfiguration.getPartners(
                Constants.PARTNER_TYPE_PARTNER, Constants.PARTNERCOMPARATOR);

        request.setAttribute("partners", partners);

        return actionMapping.findForward( ACTION_FORWARD_SUCCESS );
    }

    private String toJson(Map<?, Integer> map) {
        StringBuilder result = new StringBuilder("{");
        for (Map.Entry<?, Integer> entry : map.entrySet()) {
            if (result.length() > 1) {
                result.append(",");
            }
            Object key = entry.getKey();
            if (key instanceof Date) {
                SimpleDateFormat ebXMLDateFormat = new SimpleDateFormat( "ddMMyyyy'T'HHmmss'Z'" );
                key = ebXMLDateFormat.format(key);
            }
            result.append("'");
            result.append(key);
            result.append("':");
            result.append(entry.getValue());
        }
        result.append("}");
        return result.toString();
    }

}
