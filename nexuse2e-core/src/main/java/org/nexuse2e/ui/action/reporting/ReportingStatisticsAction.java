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

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.dao.TransactionDAO;
import org.nexuse2e.integration.info.wsdl.ConversationStatus;
import org.nexuse2e.pojo.*;
import org.nexuse2e.reporting.*;
import org.nexuse2e.ui.form.ReportingPropertiesForm;
import org.nexuse2e.ui.form.ReportingSettingsForm;
import org.nexuse2e.util.DateUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.*;

/**
 * Fills the context for the statistics report(s).
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class ReportingStatisticsAction extends ReportingAction {

    private static final int TRANSMISSION_AGO_THRESHOLD_WEEKS = 2;
    private static final int CONV_IDLE_THRESHOLD_MINUTES = 10;

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
        Statistics statistics = transactionDAO.getStatistics(timestamp, null);

        ReportingSettingsForm reportingSettings = new ReportingSettingsForm();
        ReportingPropertiesForm form = (ReportingPropertiesForm) actionForm;
        fillForm(engineConfiguration, reportingSettings, form);

        List<StatisticsMessage> statisticMessages = statistics.getMessages();

        List<PartnerPojo> partnerPojos = engineConfiguration.getPartners(
                Constants.PARTNER_TYPE_PARTNER, Constants.PARTNERCOMPARATOR);
        List<StatisticsPartner> partners = getStatisticsPartners(partnerPojos);

        List<ConversationPojo> conversations = statistics.getConversations();
        List<StatisticsConversation> idleConversations = filterIdleConversations(statistics.getIdleConversations());
        Map<String, Integer> conversationStatusCounts = getConversationCounts(conversations);

        List<ChoreographyPojo> choreographyPojos = engineConfiguration.getChoreographies();
        List<StatisticsChoreography> choreographies = getStatisticsChoreographies(choreographyPojos);

        Set<StatisticsCertificate> certificates = getStatisticsCertificates(choreographyPojos);

        request.setAttribute("choreographies", choreographies);
        request.setAttribute("conversationStatusCounts", conversationStatusCounts);
        request.setAttribute("conversationStatusTotal", conversations.size());
        request.setAttribute("idleConversations", idleConversations);
        request.setAttribute("thresholdMinutes", CONV_IDLE_THRESHOLD_MINUTES);
        request.setAttribute("messages", statisticMessages);
        request.setAttribute("certificates", certificates);
        request.setAttribute("partners", partners);

        return actionMapping.findForward(ACTION_FORWARD_SUCCESS);
    }

    private List<StatisticsConversation> filterIdleConversations(List<StatisticsConversation> conversations) {
        List<StatisticsConversation> idleForTooLongConversations = new LinkedList<>();
        for (StatisticsConversation conversation : conversations) {
            if (isIdleForTooLong(conversation)) {
                idleForTooLongConversations.add(conversation);
            }
        }
        return idleForTooLongConversations;
    }

    private boolean isIdleForTooLong(StatisticsConversation conversation) {
        Date thresholdDate = getCurrentDateMinus(Calendar.MINUTE, CONV_IDLE_THRESHOLD_MINUTES);
        return conversation.getModifiedDate().before(thresholdDate);
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
            return "no messages in " + TRANSMISSION_AGO_THRESHOLD_WEEKS + " weeks";
        }
        return DateUtil.getDiffTimeRounded(date, new Date()) + " ago";
    }

    private boolean neverOrTooLongAgo(Date date) {
        return date == null || date.before(getCurrentDateMinus(Calendar.DATE, TRANSMISSION_AGO_THRESHOLD_WEEKS));
    }

    private Date getCurrentDateMinus(int field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(field, -amount * 7);
        return calendar.getTime();
    }

    private Set<StatisticsCertificate> getStatisticsCertificates(List<ChoreographyPojo> choreographyPojos) {
        Set<StatisticsCertificate> certificates = new TreeSet<>();
        for (ChoreographyPojo choreographyPojo : choreographyPojos) {
            List<ParticipantPojo> participants = choreographyPojo.getParticipants();
            for (ParticipantPojo participantPojo : participants) {
                ConnectionPojo connection = participantPojo.getConnection();
                CertificatePojo partnerCertificate = connection.getCertificate();
                CertificatePojo localCertificate = participantPojo.getLocalCertificate();
                if (localCertificate != null) {
                    certificates.add(new StatisticsCertificate(localCertificate, true));
                }
                if (partnerCertificate != null) {
                    certificates.add(new StatisticsCertificate(partnerCertificate, false));
                }
            }
        }
        return certificates;
    }

    private Map<String, Integer> getConversationCounts(List<ConversationPojo> conversations) {
        LinkedHashMap<String, Integer> statusCounts = new LinkedHashMap<>();
        for (ConversationStatus conversionStatus : ConversationStatus.values()) {
            statusCounts.put(conversionStatus.name().toLowerCase(), 0);
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
