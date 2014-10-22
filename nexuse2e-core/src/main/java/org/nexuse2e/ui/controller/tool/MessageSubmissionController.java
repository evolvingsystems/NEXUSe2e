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
package org.nexuse2e.ui.controller.tool;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.dao.TransactionDAO;
import org.nexuse2e.pojo.ActionPojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.ParticipantPojo;
import org.nexuse2e.pojo.PartnerPojo;
import org.nexuse2e.ui.form.MessageSubmissionForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * Message submission controller.
 *
 * @author Jonas Reese
 */
@Controller
public class MessageSubmissionController {

    @RequestMapping("/MessageSubmission.do")
    public String messageSubmission(
            Model model,
            @RequestParam(value = "Submit", defaultValue = "") String submit,
            MessageSubmissionForm form,
            BindingResult result,
            EngineConfiguration engineConfiguration) throws NexusException {

        SortedSet<String> choreographyIds = new TreeSet<String>();
        
        // Set current list of choroegraphies
        List<ChoreographyPojo> choreographyList = engineConfiguration.getChoreographies();
        Iterator<ChoreographyPojo> choreographyIterator = choreographyList.iterator();
        while ( choreographyIterator.hasNext() ) {
            choreographyIds.add( choreographyIterator.next().getName() );
        }
        form.setChoreographies( choreographyIds );

        // Set current list of actions
        // Set current list of receivers
        String choreographyId = form.getChoreographyId();
        SortedSet<String> actions = new TreeSet<String>();
        SortedSet<PartnerPojo> receiverList = new TreeSet<PartnerPojo>( Constants.PARTNERCOMPARATOR );
        if ( ( ( choreographyId == null ) || ( choreographyId.length() == 0 ) ) && !choreographyIds.isEmpty() ) {
            choreographyId = choreographyIds.iterator().next();
        }
        if ( ( choreographyId != null ) && ( choreographyId.length() != 0 ) ) {
            ChoreographyPojo choreography = engineConfiguration
                    .getChoreographyByChoreographyId( choreographyId );
            if ( choreography != null ) {
                // Actions
                Set<ActionPojo> actionSet = choreography.getActions();
                Iterator<ActionPojo> actionIterator = actionSet.iterator();
                while ( actionIterator.hasNext() ) {
                    actions.add( actionIterator.next().getName() );
                }
                // Receivers
                Collection<ParticipantPojo> participantSet = choreography.getParticipants();
                Iterator<ParticipantPojo> participantIterator = participantSet.iterator();
                while ( participantIterator.hasNext() ) {
                    receiverList.add( participantIterator.next().getPartner() );
                    // LOG.trace( "Receiver: " + receiverList.get( receiverList.size() - 1 ) );
                }
            }
        }
        form.setActions( actions );
        form.setReceivers( receiverList );
        

        // Check whether we need to submit the message(s)
        if ( "Submit".equals( submit ) ) {
            // LOG.trace( "Submitting message!" );
            String conversationId = form.getConversationId();
            int receiverNxPartnerId = form.getReceiver();

            PartnerPojo partner = engineConfiguration.getPartnerByNxPartnerId(receiverNxPartnerId );
            if ( partner == null ) {
                result.reject("generic.error", new Object[] { "no partner found for receiverNxPartnerId: " + receiverNxPartnerId }, null);
            }

            String action = form.getActionId();
            String primaryKey = form.getPrimaryKey();
            CommonsMultipartFile payload1 = form.getPayloadFile1();
            if (payload1 == null || payload1.getBytes() == null || payload1.getBytes().length == 0) {
                payload1 = null;
            }

            if (payload1 == null && StringUtils.isBlank(primaryKey)) {
                result.rejectValue("primaryKey", "messagesubmission.error.noprimarykey");
            }
            if (form.getRepeat() < 1) {
                result.rejectValue("repeat", "messagesubmission.error.norepeat");
            }
            
            if (!result.hasErrors()) {
                try {
                    List<ConversationPojo> conversations = null;
                    int repeat = form.getRepeat();
                    if (form.isSendFollowUp()) {
                        ChoreographyPojo choreo = Engine.getInstance().getCurrentConfiguration().getChoreographyByChoreographyId( choreographyId );
                        if (choreo != null) {
                            conversations = Engine.getInstance().getTransactionService().getConversationsForReport(
                                    Integer.toString( Constants.CONVERSATION_STATUS_IDLE ),
                                    choreo.getNxChoreographyId(), partner.getNxPartnerId(), null, null, null, form.getRepeat(), 0, TransactionDAO.SORT_CREATED, true );
                            repeat = conversations.size();
                        }
                    }
                    
                    
                    for ( int i = 0; i < repeat; i++ ) {
                        String convId = conversationId;
                        if (payload1 != null) {
                            String label = org.nexuse2e.Constants.NX_LABEL_FILE_NAME + "|" + payload1.getOriginalFilename();
                            if (conversations != null && i < conversations.size()) {
                                convId = conversations.get( i ).getConversationId();
                            }
                            
                            
                            Engine.getInstance().getCurrentConfiguration().getBackendPipelineDispatcher().processMessage(
                                    partner.getPartnerId(), choreographyId, action, convId, label, null,
                                    payload1.getBytes() );
                            // Set primaryKey for UI confirmation message
                            primaryKey = payload1.getOriginalFilename();
                        } else {
                            Engine.getInstance().getCurrentConfiguration().getBackendPipelineDispatcher().processMessage(
                                    partner.getPartnerId(), choreographyId, action, convId, null, primaryKey, null );
                        }
                        if ( form.getRepeat() > 1 ) {
                            convId = null;
                            form.setConversationId( convId );
                        } else {
                            form.setConversationId( convId );
                        }
                    } // for
                    
                    model.addAttribute("primaryKey", primaryKey);
                } catch (Exception ex) {
                    result.reject("generic.error", new Object[] { ex.getMessage() }, null);
                }
            }
        }

        return "pages/tools/message_submission";
    }
}
