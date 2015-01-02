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
package org.nexuse2e.ui.controller.reporting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.dao.TransactionDAO;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.messaging.MessageHandlingCenter;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.pojo.PartnerPojo;
import org.nexuse2e.ui.form.ReportConversationEntryForm;
import org.nexuse2e.ui.form.ReportMessageEntryForm;
import org.nexuse2e.ui.form.ReportingPropertiesForm;
import org.nexuse2e.ui.form.ReportingSettingsForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * Controller for reporting functionality.
 * 
 * @author Jonas Reese
 */
@Controller
@SessionAttributes("reportingPropertiesForm")
public class ReportingController extends AbstractReportingController {
    private static Logger LOG = Logger.getLogger(ReportingController.class);


    @RequestMapping("/ProcessConversationReport.do")
    public String processConversationReport(
            Model model,
            HttpServletRequest request,
            EngineConfiguration engineConfiguration,
            ReportingPropertiesForm form)
                    throws NexusException {
        
        ReportingSettingsForm reportingSettings = new ReportingSettingsForm();
        fillForm(engineConfiguration, reportingSettings, form);

        List<String> participants = new ArrayList<>();
        List<PartnerPojo> partners = engineConfiguration.getPartners(Constants.PARTNER_TYPE_PARTNER, Constants.PARTNERCOMPARATOR);
        for (PartnerPojo partner : partners) {
            participants.add(partner.getPartnerId());
        }
        form.setParticipantIds(participants);
        List<String> choreographyIds = new ArrayList<>();
        List<ChoreographyPojo> choreographies = engineConfiguration.getChoreographies();
        for (ChoreographyPojo choreography : choreographies) {
            choreographyIds.add(choreography.getName());
        }
        form.setChoreographyIds(choreographyIds);

        String timezone = form.getTimezone();

        String command = form.getCommand();
        String refresh = request.getParameter("refresh");
        if (refresh != null) {
            command = "transaction";
        }

        boolean purge = false;
        String type = StringUtils.isNotBlank(request.getParameter("type")) ? request.getParameter("type") : form.getType();
        if (StringUtils.isNotBlank(type) && "purge".equals(type)) {
            purge = true;
        }
        
        form.setType(type);
        
        String searchFor = form.getSearchFor();

        if (searchFor != null && searchFor.equals("message") && command != null && command.equals("requeue")) {
            String[] values = form.getSelected();
            for (String messageIdent : values) {
                StringTokenizer st = new StringTokenizer(messageIdent, "|");
                String participantId = st.nextToken();
                String choreographyId = st.nextToken();
                st.nextToken(); // conversationId
                String messageId = st.nextToken();

                MessageContext messageContext = Engine.getInstance().getTransactionService().getMessageContext(messageId);

                if (messageContext != null) {
                    if (messageContext.getMessagePojo().isOutbound()) {
                        LOG.debug("Re-queueing outbound message " + messageId + " for choreography " + choreographyId + " and participant " + participantId);
                        MessageHandlingCenter.getInstance().requeueMessage(messageContext);
                    } else {
                        LOG.debug("Re-queueing inbound message " + messageId + " for choreography " + choreographyId + " and participant " + participantId);
                        MessageHandlingCenter.getInstance().requeueMessage(messageContext);
                    }
                }
            }

            command = "transaction";
        }
        if (searchFor != null && searchFor.equals("message") && command != null && command.equals("stop")) {
            String[] values = form.getSelected();
            for (String messageIdent : values) {
                StringTokenizer st = new StringTokenizer(messageIdent, "|");
                st.nextToken(); // participant
                st.nextToken(); // choreography
                st.nextToken(); // conversation
                String message = st.nextToken();

                Engine.getInstance().getTransactionService().stopProcessingMessage(message);
            }
            command = "transaction";
        }
        if (searchFor != null && searchFor.equals("conversation") && command != null && command.equals("delete")) {
            System.out.println("deleting conversation");

            String[] values = form.getSelected();
            for (String messageIdent : values) {

                StringTokenizer st = new StringTokenizer(messageIdent, "|");

                String participantId = st.nextToken();
                String choreographyId = st.nextToken();
                String conversationId = st.nextToken();

                System.out.println("delete: " + participantId + "," + choreographyId + "," + conversationId);
                ConversationPojo conversation = Engine.getInstance().getTransactionService().getConversation(conversationId);

                if (conversation != null) {
                    Engine.getInstance().getTransactionService().deleteConversation(conversation);
                }

            }

            command = "transaction";
        }

        String status = null;
        if (form.getStatus() != null && !form.getStatus().equals("") && !form.getStatus().startsWith("all")) {
            status = form.getStatus();
        }
        String participantId = null;
        if (form.getParticipantId() != null && !form.getParticipantId().equals("")) {
            participantId = form.getParticipantId();
        }

        String choreographyId = null;
        if (form.getChoreographyId() != null && !form.getChoreographyId().equals("")) {
            choreographyId = form.getChoreographyId();
        }

        String conversationId = null;
        boolean convActive = form.isConversationEnabled();
        if (convActive && form.getConversationId() != null && !form.getConversationId().equals("")) {
            conversationId = form.getConversationId();
        }

        String messageId = null;
        boolean messageActive = form.isMessageEnabled();
        if (messageActive && form.getMessageId() != null && !form.getMessageId().equals("")) {
            messageId = form.getMessageId();
        }
        int items = 0;
        List<Object> logItems = new ArrayList<>();

        int nxPartnerId = 0;
        int nxChoreographyId = 0;

        if (participantId != null) {
            PartnerPojo partner = engineConfiguration.getPartnerByPartnerId(participantId);
            if (partner != null) {
                nxPartnerId = partner.getNxPartnerId();
            }
        }
        if (choreographyId != null) {
            ChoreographyPojo choreography = engineConfiguration.getChoreographyByChoreographyId(choreographyId);
            if (choreography != null) {
                nxChoreographyId = choreography.getNxChoreographyId();
            }
        }

        if (searchFor != null && searchFor.equals("message")) {

            items = Engine.getInstance().getTransactionService()
                .getMessagesCount(status, nxChoreographyId, nxPartnerId, conversationId, messageId, getStartDate(form), getEndDate(form));

            form.setAllItemsCount(items);
            List<MessagePojo> reportMessages = null;
            if (StringUtils.isNotBlank(command) && "purge".equals(command) && purge) {
                reportMessages = Engine.getInstance().getTransactionService()
                    .getMessagesForReport(status, nxChoreographyId, nxPartnerId, conversationId, messageId, null, getStartDate(form), getEndDate(form),
                                          Integer.MAX_VALUE, 0, TransactionDAO.SORT_CREATED, false);
                for (MessagePojo message : reportMessages) {
                    Engine.getInstance().getTransactionService().deleteMessage(message);
                }
                command = "first";
            }
            if (command == null || command.equals("") || command.equals("first") || command.equals("transaction")) {
                int pos = form.getStartCount();
                if (pos == 0 || !command.equals("transaction")) {
                    reportMessages = Engine.getInstance().getTransactionService()
                        .getMessagesForReport(status, nxChoreographyId, nxPartnerId, conversationId, messageId, null, getStartDate(form), getEndDate(form),
                                              form.getPageSize(), 0, TransactionDAO.SORT_CREATED, false);
                    if (items > 0) {
                        form.setStartCount(1);
                    } else {
                        form.setStartCount(0);
                        form.setEndCount(0);
                    }
                    if (items > form.getPageSize()) {
                        form.setEndCount(form.getPageSize());
                    } else {
                        form.setEndCount(items);
                    }
                } else {
                    int page = pos / form.getPageSize();

                    reportMessages = Engine.getInstance().getTransactionService()
                        .getMessagesForReport(status, nxChoreographyId, nxPartnerId, conversationId, messageId, null, getStartDate(form), getEndDate(form),
                                              form.getPageSize(), page, TransactionDAO.SORT_CREATED, false);

                    if (form.getStartCount() + form.getPageSize() > items) {
                        form.setEndCount(items);
                    } else {
                        form.setEndCount(form.getStartCount() + form.getPageSize() - 1);
                    }
                }
            } else if (command.equals("back")) {
                int pos = form.getStartCount();
                if (pos < form.getPageSize()) {
                    reportMessages = Engine.getInstance().getTransactionService()
                        .getMessagesForReport(status, nxChoreographyId, nxPartnerId, conversationId, messageId, null, getStartDate(form), getEndDate(form),
                                              form.getPageSize(), 0, TransactionDAO.SORT_CREATED, false);

                    form.setStartCount(1);
                    form.setEndCount(form.getPageSize());
                } else {

                    reportMessages = Engine.getInstance().getTransactionService()
                        .getMessagesForReport(status, nxChoreographyId, nxPartnerId, conversationId, messageId, null, getStartDate(form), getEndDate(form),
                                              form.getPageSize(), (pos / form.getPageSize()) - 1, TransactionDAO.SORT_CREATED, false);

                    form.setStartCount(pos - form.getPageSize());
                    form.setEndCount(form.getStartCount() + form.getPageSize() - 1);
                }
            } else if (command.equals("next")) {
                int pos = form.getStartCount();
                if (pos + 2 * form.getPageSize() >= items) {

                    reportMessages = Engine.getInstance().getTransactionService()
                        .getMessagesForReport(status, nxChoreographyId, nxPartnerId, conversationId, messageId, null, getStartDate(form), getEndDate(form),
                                              form.getPageSize(), pos / form.getPageSize() + 1, TransactionDAO.SORT_CREATED, false);

                    form.setStartCount(form.getStartCount() + form.getPageSize());
                    form.setEndCount(items);

                } else {
                    int page = pos / form.getPageSize();

                    reportMessages = Engine.getInstance().getTransactionService()
                        .getMessagesForReport(status, nxChoreographyId, nxPartnerId, conversationId, messageId, null, getStartDate(form), getEndDate(form),
                                              form.getPageSize(), page + 1, TransactionDAO.SORT_CREATED, false);

                    form.setStartCount(form.getStartCount() + form.getPageSize());
                    form.setEndCount(form.getStartCount() + form.getPageSize() - 1);
                }
            } else if (command.equals("last")) {

                reportMessages = Engine.getInstance().getTransactionService()
                    .getMessagesForReport(status, nxChoreographyId, nxPartnerId, conversationId, messageId, null, getStartDate(form), getEndDate(form),
                                          form.getPageSize(), items / form.getPageSize(), TransactionDAO.SORT_CREATED, false);

                form.setStartCount(items / form.getPageSize() * form.getPageSize() + 1);
                form.setEndCount(items);
            }
            if (reportMessages != null) {
                for (MessagePojo pojo : reportMessages) {
                    ReportMessageEntryForm entry = new ReportMessageEntryForm();
                    entry.setMessageProperties(pojo);
                    logItems.add(entry);
                }
            }

        } else {

            items = Engine.getInstance().getTransactionService()
                .getConversationsCount(status, nxChoreographyId, nxPartnerId, conversationId, getStartDate(form), getEndDate(form), TransactionDAO.SORT_CREATED,
                                       false);

            form.setAllItemsCount(items);
            List<ConversationPojo> conversations = null;
            if (StringUtils.isNotBlank(command) && "purge".equals(command) && purge) {
                conversations = Engine.getInstance().getTransactionService()
                    .getConversationsForReport(status, nxChoreographyId, nxPartnerId, conversationId, getStartDate(form), getEndDate(form),
                                               Integer.MAX_VALUE, 0, TransactionDAO.SORT_CREATED, false);
                for (ConversationPojo conv : conversations) {
                    Engine.getInstance().getTransactionService().deleteConversation(conv);
                }
                command = "first";
            }
            if (command == null || command.equals("") || command.equals("first") || command.equals("transaction")) {
                int pos = form.getStartCount();
                if (pos == 0 || !"transaction".equals(command)) {

                    conversations = Engine.getInstance().getTransactionService()
                        .getConversationsForReport(status, nxChoreographyId, nxPartnerId, conversationId, getStartDate(form), getEndDate(form),
                                                   form.getPageSize(), 0, TransactionDAO.SORT_CREATED, false);
                    //LOG.trace("nxpartnerid: "+conversations.get( 0 ).getPartner().getNxPartnerId());
                    if (items > 0) {
                        form.setStartCount(1);
                    } else {
                        form.setStartCount(0);
                        form.setEndCount(0);
                    }
                    if (items > form.getPageSize()) {
                        form.setEndCount(form.getPageSize());
                    } else {
                        form.setEndCount(items);
                    }
                } else {
                    int page = pos / form.getPageSize();

                    conversations = Engine.getInstance().getTransactionService()
                        .getConversationsForReport(status, nxChoreographyId, nxPartnerId, conversationId, getStartDate(form), getEndDate(form),
                                                   form.getPageSize(), page, TransactionDAO.SORT_CREATED, false);

                    if (form.getStartCount() + form.getPageSize() > items) {
                        form.setEndCount(items);
                    } else {
                        form.setEndCount(form.getStartCount() + form.getPageSize() - 1);
                    }
                }
            } else if ("back".equals(command)) {
                int pos = form.getStartCount();
                if (pos < form.getPageSize()) {

                    conversations = Engine.getInstance().getTransactionService()
                        .getConversationsForReport(status, nxChoreographyId, nxPartnerId, conversationId, getStartDate(form), getEndDate(form),
                                                   form.getPageSize(), 0, TransactionDAO.SORT_CREATED, false);

                    form.setStartCount(1);
                    form.setEndCount(form.getPageSize());
                } else {

                    conversations = Engine.getInstance().getTransactionService()
                        .getConversationsForReport(status, nxChoreographyId, nxPartnerId, conversationId, getStartDate(form), getEndDate(form),
                                                   form.getPageSize(), (pos / form.getPageSize()) - 1, TransactionDAO.SORT_CREATED, false);

                    form.setStartCount(pos - form.getPageSize());
                    form.setEndCount(form.getStartCount() + form.getPageSize() - 1);
                }
            } else if ("next".equals(command)) {
                int pos = form.getStartCount();
                if (pos + 2 * form.getPageSize() >= items) {

                    conversations = Engine.getInstance().getTransactionService()
                        .getConversationsForReport(status, nxChoreographyId, nxPartnerId, conversationId, getStartDate(form), getEndDate(form),
                                                   form.getPageSize(), pos / form.getPageSize() + 1, TransactionDAO.SORT_CREATED, false);

                    form.setStartCount(form.getStartCount() + form.getPageSize());
                    form.setEndCount(items);

                } else {
                    int page = pos / form.getPageSize();

                    conversations = Engine.getInstance().getTransactionService()
                        .getConversationsForReport(status, nxChoreographyId, nxPartnerId, conversationId, getStartDate(form), getEndDate(form),
                                                   form.getPageSize(), page + 1, TransactionDAO.SORT_CREATED, false);

                    form.setStartCount(form.getStartCount() + form.getPageSize());
                    form.setEndCount(form.getStartCount() + form.getPageSize() - 1);
                }
            } else if ("last".equals(command)) {

                int page = 0;
                if (items > 0) {
                    if (items % form.getPageSize() == 0) {
                        page = items / form.getPageSize() - 1;
                    } else {
                        page = items / form.getPageSize();
                    }
                }
                conversations = Engine.getInstance().getTransactionService()
                    .getConversationsForReport(status, nxChoreographyId, nxPartnerId, conversationId, getStartDate(form), getEndDate(form), form.getPageSize(),
                                               page, TransactionDAO.SORT_CREATED, false);

                form.setStartCount(page * form.getPageSize() + 1);
                form.setEndCount(items);
            }
            if (conversations != null) {
                for (ConversationPojo pojo : conversations) {
                    ReportConversationEntryForm entry = new ReportConversationEntryForm();
                    entry.setValues(pojo);
                    entry.setTimezone(timezone);
                    logItems.add(entry);
                }
            }
        }
        if (form.getStartCount() > 1) {
            form.setFirstActive(true);
            form.setPrevActive(true);
        } else {
            form.setFirstActive(false);
            form.setPrevActive(false);
        }
        if (items > form.getEndCount()) {
            form.setNextActive(true);
            form.setLastActive(true);
        } else {
            form.setNextActive(false);
            form.setLastActive(false);
        }

        model.addAttribute("collection", logItems);
        model.addAttribute("reportingSettingsForm", reportingSettings);
        model.addAttribute("reportingPropertiesForm", form);

        return "pages/reporting/transaction_report";
    }

    /**
     * @param form
     * @return
     */
    private Date getEndDate(ReportingPropertiesForm form) {

        if (!form.isEndEnabled()) {
            return null;
        }

        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.YEAR, Integer.parseInt(form.getEndYear()));
        startCal.set(Calendar.MONTH, Integer.parseInt(form.getEndMonth()) - 1);
        startCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(form.getEndDay()));
        startCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(form.getEndHour()));
        startCal.set(Calendar.MINUTE, Integer.parseInt(form.getEndMin()));
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        return startCal.getTime();
    }

    /**
     * @param form
     * @return
     */
    private Date getStartDate(ReportingPropertiesForm form) {

        if (!form.isStartEnabled()) {
            return null;
        }
        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.YEAR, Integer.parseInt(form.getStartYear()));
        startCal.set(Calendar.MONTH, Integer.parseInt(form.getStartMonth()) - 1);
        startCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(form.getStartDay()));
        startCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(form.getStartHour()));
        startCal.set(Calendar.MINUTE, Integer.parseInt(form.getStartMin()));
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        return startCal.getTime();
    }
}
