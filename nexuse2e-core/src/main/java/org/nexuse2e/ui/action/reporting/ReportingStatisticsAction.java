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
import org.nexuse2e.reporting.CertificateStub;
import org.nexuse2e.reporting.MessageStub;
import org.nexuse2e.reporting.Statistics;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.CertificatePropertiesForm;
import org.nexuse2e.util.DateUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.nexuse2e.util.CertificateUtil.getIncludedCertificates;

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
        TransactionDAO transactionDAO = Engine.getInstance().getTransactionService().getTransactionDao();
        Statistics statistics = transactionDAO.getStatistics(timestamp, null);

        List<MessageStub> messageStubs = statistics.getMessages();
        request.setAttribute("messages", messageStubs);

        Map<String, Integer> conversationStatusCounts = getConversationCounts(statistics);
        request.setAttribute("conversationStatusCounts", toJson(conversationStatusCounts));

        List<PartnerPojo> partners = engineConfiguration.getPartners(
                Constants.PARTNER_TYPE_PARTNER, Constants.PARTNERCOMPARATOR);

        Map<String, ArrayList<CertificateStub>> certificatesPerPartner = new HashMap<>();
        Map<String, String> lastOutboundPerPartner = new HashMap<>();
        Map<String, String> lastInboundPerPartner = new HashMap<>();

        for (PartnerPojo partner : partners) {
            Set<ConnectionPojo> connections = partner.getConnections();
            ArrayList<CertificateStub> certificates = new ArrayList<>();
            for (ConnectionPojo connection : connections) {
                CertificatePojo certificate = connection.getCertificate();
                if (certificate != null) {
                    certificates.add(new CertificateStub(certificate));
                }
            }
            certificatesPerPartner.put(partner.getPartnerId(), certificates);
            lastOutboundPerPartner.put(partner.getPartnerId(), getLastSentMessageTimeDiff(partner, true));
            lastInboundPerPartner.put(partner.getPartnerId(), getLastSentMessageTimeDiff(partner, false));
        }

        List<CertificatePojo> stagedCertificates = engineConfiguration.getCertificates(CertificateType.STAGING.getOrdinal(), Constants.CERTIFICATECOMPARATOR);
        Vector<CertificatePropertiesForm> certificatePropertiesForms = getIncludedCertificates(stagedCertificates);
        List<CertificateStub> localCertificates = new LinkedList<>();

        for (CertificatePropertiesForm certificatePropertiesForm : certificatePropertiesForms) {
            localCertificates.add(new CertificateStub(certificatePropertiesForm));
        }

        request.setAttribute("partners", partners);
        request.setAttribute("lastOutboundPerPartner", lastOutboundPerPartner);
        request.setAttribute("lastInboundPerPartner", lastInboundPerPartner);
        request.setAttribute("certificatesPerPartner", certificatesPerPartner);
        request.setAttribute("localCertificates", localCertificates);

        List<ChoreographyPojo> choreographies = engineConfiguration.getChoreographies();

        Map<String, String> lastOutboundPerChoreography = new HashMap<>();
        Map<String, String> lastInboundPerChoreography = new HashMap<>();
        for (ChoreographyPojo choreography : choreographies) {
            lastOutboundPerChoreography.put(choreography.getName(), getLastSentMessageTimeDiff(choreography, true));
            lastInboundPerChoreography.put(choreography.getName(), getLastSentMessageTimeDiff(choreography, false));
        }

        request.setAttribute("choreographies", choreographies);
        request.setAttribute("lastOutboundPerChoreography", lastOutboundPerChoreography);
        request.setAttribute("lastInboundPerChoreography", lastInboundPerChoreography);

        return actionMapping.findForward( ACTION_FORWARD_SUCCESS );
    }

    private Map<String, Integer> getConversationCounts(Statistics statistics) {
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
        return conversationStatusCounts;
    }

    private String getLastSentMessageTimeDiff(PartnerPojo partner, boolean outbound) {
        TransactionDAO transactionDAO = Engine.getInstance().getTransactionService().getTransactionDao();
        // TODO set status to 3
        MessagePojo lastMessage = transactionDAO.getLastMessageByStatusPartnerAndDirection(-1, partner, outbound);
        if (lastMessage != null) {
            return DateUtil.getDiffTimeRounded(lastMessage.getCreatedDate(), new Date()) + " ago";
        } else {
            return "never";
        }
    }

    private String getLastSentMessageTimeDiff(ChoreographyPojo choreography, boolean outbound) {
        TransactionDAO transactionDAO = Engine.getInstance().getTransactionService().getTransactionDao();
        // TODO set status to 3
        MessagePojo lastMessage = transactionDAO.getLastMessageByStatusChoreographyAndDirection(-1, choreography, outbound);
        if (lastMessage != null) {
            return DateUtil.getDiffTimeRounded(lastMessage.getCreatedDate(), new Date()) + " ago";
        } else {
            return "never";
        }
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
