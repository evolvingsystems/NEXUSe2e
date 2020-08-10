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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.Engine;
import org.nexuse2e.MessageStatus;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.reporting.MessageStub;
import org.nexuse2e.reporting.Statistics;
import org.nexuse2e.ui.action.NexusE2EAction;

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
        
        // check if there are any messages that have been created after timestamp
        request.setAttribute( "messageCount", Engine.getInstance().getTransactionService().getCreatedMessagesSinceCount( timestamp ) );
        Statistics statistics = Engine.getInstance().getTransactionService().getTransactionDao().getStatistics(timestamp,null);
        request.setAttribute( "statistics", statistics );

        Map<String, Integer> choreographyCounts = new HashMap<>();
        for (MessageStub message : statistics.getMessages()) {
            Integer count = choreographyCounts.get(message.getChoreographyId());
            if(count == null) {
                choreographyCounts.put(message.getChoreographyId(),1);
            } else {
                choreographyCounts.put(message.getChoreographyId(), ++count);
            }
        }
        String[] values = toJsonArray(choreographyCounts);

        request.setAttribute( "choreographies", values[0] );
        request.setAttribute( "choreographyCounts", values[1] );


        Map<String, Integer> statusCounts = new HashMap<>();
        for (MessageStub message : statistics.getMessages()) {
            Integer count = statusCounts.get(message.getStatus().toString());
            if(count == null) {
                statusCounts.put(message.getStatus().toString(),1);
            } else {
                statusCounts.put(message.getStatus().toString(), ++count);
            }
        }
        values = toJsonArray(statusCounts);

        request.setAttribute( "status", values[0] );
        request.setAttribute( "statusCounts", values[1] );

        Map<Date, Integer> timeCounts = new HashMap<>();
        for (MessageStub message : statistics.getMessages()) {

            Date truncated = DateUtils.truncate(message.getCreatedDate(), Calendar.HOUR);

            Integer count = timeCounts.get(truncated);
            if(count == null) {
                timeCounts.put(truncated,1);
            } else {
                timeCounts.put(truncated, ++count);
            }
        }
        values = toJsonArrayDate(timeCounts);

        request.setAttribute( "times", values[0] );
        request.setAttribute( "timeCounts", values[1] );


        return actionMapping.findForward( ACTION_FORWARD_SUCCESS );
    }
    private String[] toJsonArrayDate(Map<Date, Integer> timeCounts) {
        SimpleDateFormat ebXMLDateFormat = new SimpleDateFormat( "ddMMyyyy'T'HHmmss'Z'" );
//        SimpleDateFormat ebXMLDateFormat = new SimpleDateFormat( "yyyyMMdd'T'HHmmss'Z'" );

        StringBuilder names = new StringBuilder();
        StringBuilder counts = new StringBuilder();
        for (Map.Entry<Date, Integer> dateIntegerEntry : timeCounts.entrySet()) {
            if(names.length() > 0){
                names.append(",");
                counts.append(",");
            }

            names.append("'"+ebXMLDateFormat.format(dateIntegerEntry.getKey())+"'");
            counts.append(dateIntegerEntry.getValue());
        }
        return new String[] {names.toString(),counts.toString()};
    }
    private String[] toJsonArray(Map<String, Integer> choreographyCounts) {
        StringBuilder names = new StringBuilder();
        StringBuilder counts = new StringBuilder();
        for (Map.Entry<String, Integer> stringIntegerEntry : choreographyCounts.entrySet()) {
            if(names.length() > 0){
                names.append(",");
                counts.append(",");
            }
            names.append("'"+stringIntegerEntry.getKey()+"'");
            counts.append(stringIntegerEntry.getValue());
        }
        return new String[] {names.toString(),counts.toString()};
    }

}
