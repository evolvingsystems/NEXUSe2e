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
import org.nexuse2e.configuration.CertificateType;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.dao.TransactionDAO;
import org.nexuse2e.pojo.*;
import org.nexuse2e.reporting.*;
import org.nexuse2e.ui.form.CertificatePropertiesForm;
import org.nexuse2e.ui.form.ReportingPropertiesForm;
import org.nexuse2e.ui.form.ReportingSettingsForm;
import org.nexuse2e.util.DateUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.*;

import static org.nexuse2e.util.CertificateUtil.getIncludedCertificates;

/**
 * Fills the context for the statistics report(s).
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class ReportingStatisticsAction extends ReportingAction {

    private static final Map<String, String> COLOR_MAP = buildColorMap();

    private static Map<String, String> buildColorMap() {
        Map<String, String> colorMap = new HashMap<>();
        colorMap.put("error", "#FB5012");
        colorMap.put("awaiting_ack", "#305252");
        colorMap.put("unknown", "#B6B6B6");
        colorMap.put("created", "#58A4B0");
        colorMap.put("processing", "#305252");
        colorMap.put("idle", "#58A4B0");
        colorMap.put("sending_ack", "#58A4B0");
        colorMap.put("ack_sent_awaiting_backend", "#305252");
        colorMap.put("awaiting_backend", "#305252");
        colorMap.put("backend_sent_sending_ack", "#305252");
        colorMap.put("completed", "#6DA34D");
        return colorMap;
    }

    private boolean atLeastOneCertificateConfigured;

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
        Map<String, Integer> conversationStatusCounts = getConversationCounts(conversations);

        List<PartnerPojo> partnerPojos = engineConfiguration.getPartners(
                Constants.PARTNER_TYPE_PARTNER, Constants.PARTNERCOMPARATOR);
        List<StatisticsPartner> partners = getStatisticsPartners(partnerPojos);

        List<CertificatePojo> stagedCertificates = engineConfiguration.getCertificates(CertificateType.STAGING.getOrdinal(), Constants.CERTIFICATECOMPARATOR);
        Vector<CertificatePropertiesForm> certificatePropertiesForms = getIncludedCertificates(stagedCertificates);
        List<StatisticsCertificate> localCertificates = new LinkedList<>();

        for (CertificatePropertiesForm certificatePropertiesForm : certificatePropertiesForms) {
            localCertificates.add(new StatisticsCertificate(certificatePropertiesForm));
            atLeastOneCertificateConfigured = true;
        }

        List<StatisticsChoreography> choreographies = getStatisticsChoreographies(engineConfiguration);

        request.setAttribute("colors", COLOR_MAP);
        request.setAttribute("choreographies", choreographies);
        request.setAttribute("conversationStatusCounts", conversationStatusCounts);
        request.setAttribute("conversationStatusTotal", conversations.size());
        request.setAttribute("localCertificates", localCertificates);
        request.setAttribute("messages", statisticMessages);
        request.setAttribute("atLeastOneCertificateConfigured", atLeastOneCertificateConfigured);
        request.setAttribute("partners", partners);

        return actionMapping.findForward(ACTION_FORWARD_SUCCESS);
    }

    private List<StatisticsPartner> getStatisticsPartners(List<PartnerPojo> partnerPojos) {
        List<StatisticsPartner> partners = new LinkedList<>();
        for (PartnerPojo partnerPojo : partnerPojos) {
            StatisticsPartner partner = new StatisticsPartner(partnerPojo);
            Set<ConnectionPojo> connections = partnerPojo.getConnections();
            List<StatisticsCertificate> certificates = new LinkedList<>();
            for (ConnectionPojo connection : connections) {
                CertificatePojo certificate = connection.getCertificate();
                if (certificate != null) {
                    certificates.add(new StatisticsCertificate(certificate));
                    atLeastOneCertificateConfigured = true;
                }
            }
            partner.setCertificates(certificates);
            partner.setLastInboundTime(getLastSentMessageTimeDiff(partnerPojo, false));
            partner.setLastOutboundTime(getLastSentMessageTimeDiff(partnerPojo, true));
            partners.add(partner);
        }
        return partners;
    }

    private List<StatisticsChoreography> getStatisticsChoreographies(EngineConfiguration engineConfiguration) {
        List<ChoreographyPojo> choreographyPojos = engineConfiguration.getChoreographies();
        List<StatisticsChoreography> choreographies = new LinkedList<>();
        for (ChoreographyPojo choreographyPojo : choreographyPojos) {
            StatisticsChoreography choreography = new StatisticsChoreography(choreographyPojo);
            choreography.setLastInboundTime(getLastSentMessageTimeDiff(choreographyPojo, false));
            choreography.setLastOutboundTime(getLastSentMessageTimeDiff(choreographyPojo, true));
            choreographies.add(choreography);
        }
        return choreographies;
    }

    private Map<String, Integer> getConversationCounts(List<ConversationPojo> conversations) {
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
        return conversationStatusCounts;
    }

    private String getLastSentMessageTimeDiff(PartnerPojo partner, boolean outbound) {
        TransactionDAO transactionDAO = Engine.getInstance().getTransactionService().getTransactionDao();
        MessagePojo lastMessage = transactionDAO.getLastMessageByStatusPartnerAndDirection(3, partner, outbound);
        if (lastMessage != null) {
            return DateUtil.getDiffTimeRounded(lastMessage.getCreatedDate(), new Date()) + " ago";
        } else {
            return "never";
        }
    }

    private String getLastSentMessageTimeDiff(ChoreographyPojo choreography, boolean outbound) {
        TransactionDAO transactionDAO = Engine.getInstance().getTransactionService().getTransactionDao();
        MessagePojo lastMessage = transactionDAO.getLastMessageByStatusChoreographyAndDirection(3, choreography, outbound);
        if (lastMessage != null) {
            return DateUtil.getDiffTimeRounded(lastMessage.getCreatedDate(), new Date()) + " ago";
        } else {
            return "never";
        }
    }
}
