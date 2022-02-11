/**
 * NEXUSe2e Business Messaging Open Source
 * Copyright 2000-2021, direkt gruppe GmbH
 * <p>
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation version 3 of
 * the License.
 * <p>
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.nexuse2e.ui.action.reporting;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.messaging.MessageHandlingCenter;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.ReportingPropertiesForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Requeue or stop a message
 *
 * @author guido.esch
 */
public class ModifyMessageAction extends NexusE2EAction {

    private static final String ORIGIN_DASHBOARD = "dashboard";
    private static Logger LOG = LogManager.getLogger(ModifyMessageAction.class);

    /* (non-Javadoc)
     * @see org.nexuse2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org
     * .apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http
     * .HttpServletResponse, org.apache.struts.action.ActionMessages, org.apache.struts.action.ActionMessages)
     */
    public ActionForward executeNexusE2EAction(ActionMapping actionMapping, ActionForm actionForm,
                                               HttpServletRequest request, HttpServletResponse response,
                                               EngineConfiguration engineConfiguration, ActionMessages errors,
                                               ActionMessages messages) throws Exception {

        ActionForward success = actionMapping.findForward(ACTION_FORWARD_SUCCESS);
        ActionForward error = actionMapping.findForward(ACTION_FORWARD_FAILURE);

        ReportingPropertiesForm reportingPropertiesForm = (ReportingPropertiesForm) actionForm;

        String participantId = reportingPropertiesForm.getParticipantId();
        String choreographyId = reportingPropertiesForm.getChoreographyId();
        String conversationId = reportingPropertiesForm.getConversationId();
        String messageId = reportingPropertiesForm.getMessageId();
        String action = reportingPropertiesForm.getCommand();
        String origin = reportingPropertiesForm.getOrigin();
        boolean outbound = reportingPropertiesForm.isOutbound();

        if (action == null || messageId == null) {
            ActionMessage errorMessage = new ActionMessage("generic.error", "can't modify message status");
            errors.add(ActionMessages.GLOBAL_MESSAGE, errorMessage);
            resetForm(reportingPropertiesForm);
            return error;
        }
        LOG.debug("Modify Message Action: " + action);

        if (action.equals("requeue")) {

            if (outbound) {
                LOG.debug("Requeueing outbound message " + messageId + " for choreography " + choreographyId + " and " +
                        "participant " + participantId);
                try {
                    MessageHandlingCenter.getInstance().requeueMessage(messageId);
                } catch (Exception e) {
                    ActionMessage errorMessage = new ActionMessage("generic.error", e.getMessage());
                    errors.add(ActionMessages.GLOBAL_MESSAGE, errorMessage);
                }
            } else {
                LOG.debug("Requeueing inbound message " + messageId + " for choreography " + choreographyId + " and " +
                        "participant " + participantId);
                try {
                    MessageHandlingCenter.getInstance().requeueMessage(messageId);
                } catch (Exception e) {
                    ActionMessage errorMessage = new ActionMessage("generic.error", e.getMessage());
                    errors.add(ActionMessages.GLOBAL_MESSAGE, errorMessage);
                }
            }
        } else if (action.equals("stop")) {

            LOG.debug("Stopping message " + messageId + " for choreography " + choreographyId + " and participant " + participantId);
            // Message newMessage = MessageJDBCPersistent.getMessage( choreography, participant, conversation,
            // message );
            try {
                Engine.getInstance().getTransactionService().stopProcessingMessage(messageId);
            } catch (Exception e) {
                ActionMessage errorMessage = new ActionMessage("generic.error", e.getMessage());
                errors.add(ActionMessages.GLOBAL_MESSAGE, errorMessage);
            }

        }

        resetForm(reportingPropertiesForm);
        return ORIGIN_DASHBOARD.equals(origin) ? new ActionForward() : success;
    }

    private void resetForm(ReportingPropertiesForm reportingPropertiesForm) {
        reportingPropertiesForm.setParticipantId(null);
        reportingPropertiesForm.setMessageId(null);
        reportingPropertiesForm.setConversationId(null);
        reportingPropertiesForm.setChoreographyId(null);
        reportingPropertiesForm.setConversationEnabled(false);
        reportingPropertiesForm.setMessageEnabled(false);
        reportingPropertiesForm.setOrigin(null);
    }
} // ModifyMessageAction
