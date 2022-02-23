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
package org.nexuse2e.ui.action.reporting;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.ConversationStatus;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.dao.TransactionDAO;

import org.nexuse2e.pojo.*;
import org.nexuse2e.reporting.*;
import org.nexuse2e.ui.form.ReportingPropertiesForm;
import org.nexuse2e.ui.form.ReportingSettingsForm;
import org.nexuse2e.util.DateUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.*;

import static org.nexuse2e.util.DateUtil.getCurrentDateMinusWeeks;

/**
 * Fills the context for the statistics report(s).
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class ReportingStatisticsAction extends ReportingAction {

    private int transactionActivityTimeframeInWeeks = 2;

    @Override
    public ActionForward executeNexusE2EAction(ActionMapping actionMapping,
                                               ActionForm actionForm, HttpServletRequest request,
                                               HttpServletResponse response,
                                               EngineConfiguration engineConfiguration,
                                               ActionMessages errors,
                                               ActionMessages messages) throws Exception {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
        TransactionDAO transactionDAO = Engine.getInstance().getTransactionService().getTransactionDao();

        int idleGracePeriodInMinutes = Engine.getInstance().getIdleGracePeriodInMinutes();
        transactionActivityTimeframeInWeeks = Engine.getInstance().getTransactionActivityTimeframeInWeeks();

        Statistics statistics = transactionDAO.getStatistics(timestamp, null);

        ReportingSettingsForm reportingSettings = new ReportingSettingsForm();
        ReportingPropertiesForm form = (ReportingPropertiesForm) actionForm;
        fillForm(engineConfiguration, reportingSettings, form);

        List<StatisticsMessage> statisticMessages = statistics.getFailedMessages();

        List<PartnerPojo> partnerPojos = engineConfiguration.getPartners(
                Constants.PARTNER_TYPE_PARTNER, Constants.PARTNERCOMPARATOR);
        List<StatisticsPartner> partners = getStatisticsPartners(partnerPojos);

        List<ConversationPojo> conversations = statistics.getConversations();
        List<StatisticsConversation> idleConversations = statistics.getIdleConversations();
        Map<String, Integer> conversationStatusCounts = getConversationCounts(conversations);

        List<ChoreographyPojo> choreographyPojos = engineConfiguration.getChoreographies();
        List<StatisticsChoreography> choreographies = getStatisticsChoreographies(choreographyPojos);

        Set<StatisticsCertificate> certificates = transactionDAO.getStatisticsCertificates(choreographyPojos);

        request.setAttribute("choreographies", choreographies);
        request.setAttribute("conversationStatusCounts", conversationStatusCounts);
        request.setAttribute("conversationStatusTotal", conversations.size());
        request.setAttribute("idleConversations", idleConversations);
        request.setAttribute("idleGracePeriodInMinutes", idleGracePeriodInMinutes);
        request.setAttribute("transactionActivityTimeframeInWeeks", transactionActivityTimeframeInWeeks);
        request.setAttribute("messages", statisticMessages);
        request.setAttribute("certificates", certificates);
        request.setAttribute("partners", partners);

        return actionMapping.findForward(ACTION_FORWARD_SUCCESS);
    }



    private List<StatisticsPartner> getStatisticsPartners(List<PartnerPojo> partnerPojos) {
        List<StatisticsPartner> partners = new LinkedList<>();
        for (PartnerPojo partnerPojo : partnerPojos) {
            StatisticsPartner partner = new StatisticsPartner(partnerPojo);
            Date lastInboundMessageTime = getLastSentMessageTime(partnerPojo, false);
            Date lastOutboundMessageTime = getLastSentMessageTime(partnerPojo, true);
            if (!neverOrTooLongAgo(lastInboundMessageTime) || !neverOrTooLongAgo(lastOutboundMessageTime)) {
                partner.setLastInboundTime(formatTimeDifference(lastInboundMessageTime));
                partner.setLastOutboundTime(formatTimeDifference(lastOutboundMessageTime));
                partners.add(partner);
            }
        }
        return partners;
    }

    private List<StatisticsChoreography> getStatisticsChoreographies(List<ChoreographyPojo> choreographyPojos) {
        List<StatisticsChoreography> choreographies = new LinkedList<>();
        for (ChoreographyPojo choreographyPojo : choreographyPojos) {
            StatisticsChoreography choreography = new StatisticsChoreography(choreographyPojo);
            Date lastInboundMessageTime = getLastSentMessageTime(choreographyPojo, false);
            Date lastOutboundMessageTime = getLastSentMessageTime(choreographyPojo, true);
            if (!neverOrTooLongAgo(lastInboundMessageTime) || !neverOrTooLongAgo(lastOutboundMessageTime)) {
                choreography.setLastInboundTime(formatTimeDifference(lastInboundMessageTime));
                choreography.setLastOutboundTime(formatTimeDifference(lastOutboundMessageTime));
                choreographies.add(choreography);
            }
        }
        return choreographies;
    }

    private String formatTimeDifference(Date date) {
        if (neverOrTooLongAgo(date)) {
            return "no messages in " + transactionActivityTimeframeInWeeks + " weeks";
        }
        return DateUtil.getDiffTimeRounded(date, new Date()) + " ago";
    }

    private boolean neverOrTooLongAgo(Date date) {
        return date == null || date.before(getCurrentDateMinusWeeks(transactionActivityTimeframeInWeeks));
    }

    private Map<String, Integer> getConversationCounts(List<ConversationPojo> conversations) {
        LinkedHashMap<String, Integer> statusCounts = new LinkedHashMap<>();
        ConversationStatus[] statuses = ConversationStatus.values();
        for (int i = statuses.length - 1; i >= 0; i--) {
            statusCounts.put(statuses[i].name().toLowerCase(), 0);
        }
        for (ConversationPojo conversation : conversations) {
            String status = conversation.getStatusName().toLowerCase();
            statusCounts.put(status, statusCounts.get(status) + 1);
        }
        return statusCounts;
    }

    private Date getLastSentMessageTime(PartnerPojo partner, boolean outbound) {
        TransactionDAO transactionDAO = Engine.getInstance().getTransactionService().getTransactionDao();
        MessagePojo lastMessage = transactionDAO.getLastSuccessfulMessageByPartnerAndDirection(partner, outbound);
        if (lastMessage != null) {
            return lastMessage.getCreatedDate();
        } else {
            return null;
        }
    }

    private Date getLastSentMessageTime(ChoreographyPojo choreography, boolean outbound) {
        TransactionDAO transactionDAO = Engine.getInstance().getTransactionService().getTransactionDao();
        MessagePojo lastMessage = transactionDAO.getLastSuccessfulMessageByChoreographyAndDirection(choreography, outbound);
        if (lastMessage != null) {
            return lastMessage.getCreatedDate();
        } else {
            return null;
        }
    }
}
