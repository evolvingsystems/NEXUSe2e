/**
 * NEXUSe2e Business Messaging Open Source  
 * Copyright 2007, Tamgroup and X-ioma GmbH   
 *  
 * This is free software; you can redistribute it and/or modify it  
 * under the terms of the GNU Lesser General Public License as  
 * published by the Free Software Foundation version 2.1 of  
 * the License.  
 *  
 * This software is distributed in the hope that it will be useful,  
 * but WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU  
 * Lesser General Public License for more details.  
 *  
 * You should have received a copy of the GNU Lesser General Public  
 * License along with this software; if not, write to the Free  
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.nexuse2e.messaging;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.controller.StateTransitionException;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.MessagePojo;

/**
 * This class implements a state machine for a single conversation. It verifies the validity
 * and the execution of state transitions in a thread-safe way.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class ConversationStateMachine {

    private static Logger    LOG = Logger.getLogger( ConversationStateMachine.class );

    private ConversationPojo conversation;
    private MessagePojo      message;
    private boolean          reliable;
    private Object           sync;

    /**
     * Constructs a new <code>ConversationStateMachine</code>. Please do not use the constructor,
     * use {@link MessageContext#getStateMachine()}.
     * @see MessageContext
     * @param conversation The conversation. Must not be <code>null</code>.
     * @param message The message. Must not be <code>null</code>.
     * @param reliable If <code>true</code>, reliable messaging is enabled.
     * @param sync The synchronization object. Must not be <code>null</code>.
     */
    public ConversationStateMachine( ConversationPojo conversation, MessagePojo message, boolean reliable, Object sync ) {

        this.conversation = conversation;
        this.message = message;
        this.reliable = reliable;
        this.sync = sync;
    }

    /**
     * Gets the conversation that is associated with this <code>ConversationStateMachine</code>.
     * @return The conversation.
     */
    public ConversationPojo getConversation() {

        return conversation;
    }

    public void sentMessage() throws StateTransitionException, NexusException {

        synchronized ( sync ) {
            if ( message.getType() == Constants.INT_MESSAGE_TYPE_NORMAL ) {
                if ( ( conversation.getStatus() == Constants.CONVERSATION_STATUS_PROCESSING )
                        || ( conversation.getStatus() == Constants.CONVERSATION_STATUS_AWAITING_ACK )
                        || ( conversation.getStatus() == Constants.CONVERSATION_STATUS_ERROR /* Included for requeuing */) ) {
                    if ( reliable ) {
                        conversation.setStatus( Constants.CONVERSATION_STATUS_AWAITING_ACK );
                    } else {
                        Engine.getInstance().getTransactionService().deregisterProcessingMessage(
                                message.getMessageId() );
                        message.setStatus( Constants.MESSAGE_STATUS_SENT );
                        message.setModifiedDate( new Date() );
                        if ( conversation.getCurrentAction().isEnd() ) {
                            conversation.setStatus( Constants.CONVERSATION_STATUS_COMPLETED );
                        } else {
                            conversation.setStatus( Constants.CONVERSATION_STATUS_IDLE );
                        }
                    }
                } else {
                    throw new StateTransitionException( "Unexpected conversation state after sending normal message: "
                            + conversation.getStatus() );
                }
            } else {
                // Engine.getInstance().getTransactionService().deregisterProcessingMessage( message.getMessageId() );
                message.setStatus( Constants.MESSAGE_STATUS_SENT );
                Date endDate = new Date();
                message.setModifiedDate( endDate );
                message.setEndDate( endDate );
                message.getReferencedMessage().setEndDate( endDate );
                if ( conversation.getStatus() == Constants.CONVERSATION_STATUS_SENDING_ACK
                        || conversation.getStatus() == Constants.CONVERSATION_STATUS_PROCESSING ) {
                    conversation.setStatus( Constants.CONVERSATION_STATUS_ACK_SENT_AWAITING_BACKEND );
                } else if ( ( conversation.getStatus() == Constants.CONVERSATION_STATUS_BACKEND_SENT_SENDING_ACK )
                        || ( conversation.getStatus() == Constants.CONVERSATION_STATUS_IDLE )
                        || ( conversation.getStatus() == Constants.CONVERSATION_STATUS_ACK_SENT_AWAITING_BACKEND ) ) {
                    if ( conversation.getCurrentAction().isEnd() ) {
                        conversation.setStatus( Constants.CONVERSATION_STATUS_COMPLETED );
                    } else {
                        conversation.setStatus( Constants.CONVERSATION_STATUS_IDLE );
                    }
                } else if ( conversation.getStatus() == Constants.CONVERSATION_STATUS_AWAITING_BACKEND ) {
                    LOG.debug( new LogMessage( "Received ack message, backend still processing - conversation ID: "
                            + conversation.getConversationId(), message ) );
                } else if ( ( conversation.getStatus() != Constants.CONVERSATION_STATUS_COMPLETED )
                        && ( conversation.getStatus() != Constants.CONVERSATION_STATUS_ERROR ) ) {
                    LOG.error( new LogMessage( "Unexpected conversation state after sending ack message: "
                            + conversation.getStatus(), message ) );
                }
            }

            // Persist status changes
            try {
                Engine.getInstance().getTransactionService().updateTransaction( message );
            } catch ( StateTransitionException stex ) {
                LOG.warn( stex.getMessage() );
            }
        }
    }
    
    public void receivedRequestMessage() throws StateTransitionException, NexusException {

        synchronized ( sync ) {
            List<MessagePojo> messages = conversation.getMessages();

            message.setStatus( Constants.MESSAGE_STATUS_SENT );
            message.setModifiedDate( new Date() );

            conversation.setStatus( Constants.CONVERSATION_STATUS_PROCESSING );
            if ( message.getNxMessageId() <= 0 ) {
                messages.add( message );
                Engine.getInstance().getTransactionService().storeTransaction( conversation, message );
            } else {
                Engine.getInstance().getTransactionService().updateTransaction( message );
            }
        } // synchronized
    }

    public void receivedNonReliableMessage() throws StateTransitionException, NexusException {

        synchronized ( sync ) {
            if ( message.getConversation().getCurrentAction().isEnd() ) {
                message.getConversation().setStatus( Constants.CONVERSATION_STATUS_COMPLETED );
            } else {
                message.getConversation().setStatus( Constants.CONVERSATION_STATUS_IDLE );
            }
            // Persist status changes
            Engine.getInstance().getTransactionService().updateTransaction( message );
        }
    }

    public void receivedAckMessage() throws StateTransitionException, NexusException {

        synchronized ( sync ) {
            MessagePojo referencedMessage = message.getReferencedMessage();
            if ( referencedMessage != null ) {
                if ( ( referencedMessage.getConversation().getStatus() == Constants.CONVERSATION_STATUS_AWAITING_ACK )
                        || ( referencedMessage.getConversation().getStatus() == Constants.CONVERSATION_STATUS_PROCESSING )
                        || ( referencedMessage.getConversation().getStatus() == Constants.CONVERSATION_STATUS_ERROR ) ) {
                    if ( referencedMessage.getConversation().getCurrentAction().isEnd() ) {
                        referencedMessage.getConversation().setStatus( Constants.CONVERSATION_STATUS_COMPLETED );
                    } else {
                        referencedMessage.getConversation().setStatus( Constants.CONVERSATION_STATUS_IDLE );
                    }
                    referencedMessage.setStatus( Constants.MESSAGE_STATUS_SENT );

                    // Complete ack message and add to conversation
                    Date endDate = new Date();
                    message.setAction( referencedMessage.getAction() );
                    message.setStatus( Constants.MESSAGE_STATUS_SENT );
                    message.setModifiedDate( endDate );
                    message.setEndDate( endDate );
                    referencedMessage.getConversation().getMessages().add( message );
                    referencedMessage.setModifiedDate( endDate );
                    referencedMessage.setEndDate( endDate );

                    Engine.getInstance().getTransactionService().updateTransaction( referencedMessage );
                } else {
                    throw new StateTransitionException(
                            "Ack message received where it was not expected: Message status was "
                                    + referencedMessage.getStatus() );
                }
            } else {
                throw new NexusException( "Error using referenced message on acknowledgment (ack message ID: "
                        + message.getMessageId() + ")" );
            }
        }
    }

    public void receivedErrorMessage() throws StateTransitionException, NexusException {

        synchronized ( sync ) {

            MessagePojo referencedMessage = message.getReferencedMessage();
            if ( referencedMessage != null ) {

                referencedMessage.getConversation().setStatus( Constants.CONVERSATION_STATUS_ERROR );
                referencedMessage.setStatus( Constants.MESSAGE_STATUS_FAILED );

                // Complete error message and add to conversation
                Date endDate = new Date();
                message.setAction( referencedMessage.getAction() );
                message.setStatus( Constants.MESSAGE_STATUS_SENT );
                message.setModifiedDate( endDate );
                message.setEndDate( endDate );
                referencedMessage.getConversation().getMessages().add( message );
                referencedMessage.setModifiedDate( endDate );
                referencedMessage.setEndDate( endDate );
                try {
                    Engine.getInstance().getTransactionService().updateTransaction( referencedMessage );
                } catch ( StateTransitionException stex ) {
                    LOG.warn( stex.getMessage() );
                }
            } else {
                throw new NexusException(
                        "Error using referenced message on negative acknowledgment (error message ID: "
                                + message.getMessageId() + ")" );
            }

        } // synchronized
    }

    public void processedBackend() throws StateTransitionException, NexusException {

        synchronized ( sync ) {
            message.setStatus( Constants.MESSAGE_STATUS_SENT );
            message.setModifiedDate( new Date() );
            if ( ( conversation.getStatus() == Constants.CONVERSATION_STATUS_ACK_SENT_AWAITING_BACKEND )
                    || ( conversation.getStatus() == Constants.CONVERSATION_STATUS_ERROR ) // requeued message
                    || ( conversation.getStatus() == Constants.CONVERSATION_STATUS_IDLE ) ) {
                if ( conversation.getCurrentAction().isEnd() ) {
                    conversation.setStatus( Constants.CONVERSATION_STATUS_COMPLETED );
                } else {
                    conversation.setStatus( Constants.CONVERSATION_STATUS_IDLE );
                }
            } else if ( conversation.getStatus() == Constants.CONVERSATION_STATUS_AWAITING_BACKEND
                    || conversation.getStatus() == Constants.CONVERSATION_STATUS_PROCESSING ) {
                conversation.setStatus( Constants.CONVERSATION_STATUS_BACKEND_SENT_SENDING_ACK );
            } else if ( conversation.getStatus() == Constants.CONVERSATION_STATUS_COMPLETED ) {
                LOG.debug( new LogMessage( "Processing message for completed conversation.", message ) );
            } else {
                LOG.error( new LogMessage( "Unexpected conversation state detected: " + conversation.getStatus(),
                        message ) );
            }

            // Persist the message
            Engine.getInstance().getTransactionService().updateTransaction( message );
        } // synchronized
    }

    public void processingFailed() throws StateTransitionException, NexusException {

        synchronized ( sync ) {
            message.setStatus( Constants.MESSAGE_STATUS_FAILED );
            conversation.setStatus( Constants.CONVERSATION_STATUS_ERROR );

            // Persist the message
            Engine.getInstance().getTransactionService().updateTransaction( message );
        } // synchronized
    }

    public void queueMessage() throws StateTransitionException, NexusException {

        synchronized ( sync ) {
            List<MessagePojo> messages = conversation.getMessages();

            message.setStatus( Constants.MESSAGE_STATUS_QUEUED );
            message.setModifiedDate( new Date() );

            if ( message.getType() == org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_NORMAL ) {
                conversation.setStatus( Constants.CONVERSATION_STATUS_PROCESSING );
            }
            if ( message.getNxMessageId() <= 0 ) {
                messages.add( message );
                Engine.getInstance().getTransactionService().storeTransaction( conversation, message );
            } else {
                Engine.getInstance().getTransactionService().updateTransaction( message );
            }
        } // synchronized
    }
}
