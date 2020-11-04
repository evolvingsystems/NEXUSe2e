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

        List<ConversationPojo> conversations = statistics.getConversations();
        List<Entry> conversationStatusCounts = getConversationCounts(conversations);

        List<PartnerPojo> partnerPojos = engineConfiguration.getPartners(
                Constants.PARTNER_TYPE_PARTNER, Constants.PARTNERCOMPARATOR);
        List<StatisticsPartner> partners = getStatisticsPartners(partnerPojos);

        List<ChoreographyPojo> choreographyPojos = engineConfiguration.getChoreographies();
        List<StatisticsChoreography> choreographies = getStatisticsChoreographies(choreographyPojos);

        Set<StatisticsCertificate> certificates = getStatisticsCertificates(choreographyPojos);

        request.setAttribute("choreographies", choreographies);
        request.setAttribute("conversationStatusCounts", conversationStatusCounts);
        request.setAttribute("conversationStatusTotal", conversations.size());
        request.setAttribute("messages", statisticMessages);
        request.setAttribute("certificates", certificates);
        request.setAttribute("partners", partners);

        return actionMapping.findForward(ACTION_FORWARD_SUCCESS);
    }

    private List<StatisticsPartner> getStatisticsPartners(List<PartnerPojo> partnerPojos) {
        List<StatisticsPartner> partners = new LinkedList<>();
        for (PartnerPojo partnerPojo : partnerPojos) {
            StatisticsPartner partner = new StatisticsPartner(partnerPojo);
            partner.setLastInboundTime(getLastSentMessageTimeDiff(partnerPojo, false));
            partner.setLastOutboundTime(getLastSentMessageTimeDiff(partnerPojo, true));
            partners.add(partner);
        }
        return partners;
    }

    private List<StatisticsChoreography> getStatisticsChoreographies(List<ChoreographyPojo> choreographyPojos) {
        List<StatisticsChoreography> choreographies = new LinkedList<>();
        for (ChoreographyPojo choreographyPojo : choreographyPojos) {
            StatisticsChoreography choreography = new StatisticsChoreography(choreographyPojo);
            choreography.setLastInboundTime(getLastSentMessageTimeDiff(choreographyPojo, false));
            choreography.setLastOutboundTime(getLastSentMessageTimeDiff(choreographyPojo, true));
            choreographies.add(choreography);
        }
        return choreographies;
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
                    certificates.add(new StatisticsCertificate(localCertificate));
                }
                if (partnerCertificate != null) {
                    certificates.add(new StatisticsCertificate(partnerCertificate));
                }
            }
        }
        return certificates;
    }

    private List<Entry> getConversationCounts(List<ConversationPojo> conversations) {
        List<Entry> statusCounts = new LinkedList<>();
        for (ConversationPojo conversation : conversations) {
            String status = conversation.getStatusName().toLowerCase();
            Entry searchEntry = new Entry(status);
            int index = statusCounts.indexOf(searchEntry);
            if (index == -1) {
                statusCounts.add(searchEntry);
            } else {
                statusCounts.get(index).increment();
            }
        }
        Collections.sort(statusCounts);
        return statusCounts;
    }

    private String getLastSentMessageTimeDiff(PartnerPojo partner, boolean outbound) {
        TransactionDAO transactionDAO = Engine.getInstance().getTransactionService().getTransactionDao();
        MessagePojo lastMessage = transactionDAO.getLastSuccessfulMessageByPartnerAndDirection(partner, outbound);
        if (lastMessage != null) {
            return DateUtil.getDiffTimeRounded(lastMessage.getCreatedDate(), new Date()) + " ago";
        } else {
            return "never";
        }
    }

    private String getLastSentMessageTimeDiff(ChoreographyPojo choreography, boolean outbound) {
        TransactionDAO transactionDAO = Engine.getInstance().getTransactionService().getTransactionDao();
        MessagePojo lastMessage = transactionDAO.getLastSuccessfulMessageByChoreographyAndDirection(choreography, outbound);
        if (lastMessage != null) {
            return DateUtil.getDiffTimeRounded(lastMessage.getCreatedDate(), new Date()) + " ago";
        } else {
            return "never";
        }
    }

    public static class Entry implements Comparable<Entry> {
        private String key;
        private Integer value;

        public Entry(String key, Integer value) {
            this.key = key;
            this.value = value;
        }

        public Entry(String key) {
            this(key, 1);
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Entry) {
                Entry e = (Entry) o;
                return Objects.equals(key, e.key);
            }
            return false;
        }

        @Override
        public int compareTo(Entry entry) {
            return entry.value - this.value;
        }

        public void increment() {
            this.value++;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }
    }
}
