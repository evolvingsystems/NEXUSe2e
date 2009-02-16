package org.nexuse2e.dao;

import java.util.Date;
import java.util.List;

import org.nexuse2e.NexusException;
import org.nexuse2e.controller.StateTransitionException;
import org.nexuse2e.pojo.ActionPojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.LogPojo;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.pojo.PartnerPojo;

public interface TransactionDAO {

    public static final int SORT_NONE     = 0;
    public static final int SORT_CREATED  = 1;
    public static final int SORT_MODIFIED = 2;
    public static final int SORT_STATUS   = 3;
    public static final int SORT_CPAID    = 4;
    public static final int SORT_ACTION   = 5;

    /**
     * Find a conversation by its identifier
     * @param conversationId The converstaion identifier
     * @return
     */
    @SuppressWarnings("unchecked")
    public abstract ConversationPojo getConversationByConversationId( String conversationId ); // getConversationByConversationId

    /**
     * @param conversationId
     * @return
     */
    @SuppressWarnings("unchecked")
    public abstract ConversationPojo getConversationByConversationId( String choreographyId, String conversationId,
            int nxPartnerId ); // getConversationByConversationId

    @SuppressWarnings("unchecked")
    public abstract MessagePojo getMessageByMessageId( String messageId ) throws NexusException;

    @SuppressWarnings("unchecked")
    public abstract MessagePojo getMessageByReferencedMessageId( String messageId ) throws NexusException;

    /**
     * @param status
     * @param nxChoreographyId
     * @param nxPartnerId
     * @param nxConversationId
     * @param messageId
     * @param startDate
     * @param endDate
     * @return
     * @throws NexusException
     */
    public abstract int getMessagesCount( String status, int nxChoreographyId, int nxPartnerId, String conversationId,
            String messageId, Date startDate, Date endDate ) throws NexusException;

    /**
     * @return
     * @throws NexusException
     */
    @SuppressWarnings("unchecked")
    public abstract List<MessagePojo> getActiveMessages() throws NexusException; // getActiveMessages

    /**
     * @param status
     * @param nxChoreographyId
     * @param nxPartnerId
     * @param nxConversationId
     * @param messageId
     * @param type
     * @param start
     * @param end
     * @param itemsPerPage
     * @param page
     * @param field
     * @param ascending
     * @return
     * @throws NexusException
     */
    @SuppressWarnings("unchecked")
    public abstract List<MessagePojo> getMessagesForReport( String status, int nxChoreographyId, int nxPartnerId,
            String conversationId, String messageId, String type, Date start, Date end, int itemsPerPage, int page,
            int field, boolean ascending ) throws NexusException;

    /**
     * @param status
     * @param nxChoreographyId
     * @param nxPartnerId
     * @param conversationId
     * @param start
     * @param end
     * @param itemsPerPage
     * @param page
     * @param field
     * @param ascending
     * @return
     * @throws NexusException
     */
    @SuppressWarnings("unchecked")
    public abstract List<ConversationPojo> getConversationsForReport( String status, int nxChoreographyId,
            int nxPartnerId, String conversationId, Date start, Date end, int itemsPerPage, int page, int field,
            boolean ascending ) throws NexusException;

    /**
     * @param start
     * @param end
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    public abstract long getConversationsCount( Date start, Date end ) throws NexusException;

    /**
     * @param start
     * @param end
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    public abstract long getLogCount( Date start, Date end ) throws NexusException;

    /**
     * @param start
     * @param end
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    public abstract long removeLogEntries( Date start, Date end ) throws NexusException;

    /**
     * @param start
     * @param end
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    public abstract long removeConversations( Date start, Date end ) throws NexusException;

    /**
     * @param status
     * @param choreographyId
     * @param participantId
     * @param conversationId
     * @param start
     * @param end
     * @param field
     * @param ascending
     * @return
     * @throws PersistenceException
     */
    public abstract int getConversationsCount( String status, int nxChoreographyId, int nxPartnerId,
            String conversationId, Date start, Date end, int field, boolean ascending ) throws NexusException;

    public abstract void storeTransaction( ConversationPojo conversationPojo, MessagePojo messagePojo )
            throws NexusException; // storeTransaction

    public abstract void reattachConversation( ConversationPojo conversationPojo ) throws NexusException;

    public abstract void updateMessage( MessagePojo messagePojo ) throws NexusException; // updateMessage

    public abstract void updateConversation( ConversationPojo conversationPojo ) throws NexusException; // updateMessage

    /**
     * @param partner
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    @SuppressWarnings("unchecked")
    public abstract List<ConversationPojo> getConversationsByPartner( PartnerPojo partner );

    /**
     * @param partner
     * @param choreography
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    @SuppressWarnings("unchecked")
    public abstract List<ConversationPojo> getConversationsByPartnerAndChoreography( PartnerPojo partner,
            ChoreographyPojo choreography ) throws NexusException;

    /**
     * @param choreography
     * @return
     */
    @SuppressWarnings("unchecked")
    public abstract List<ConversationPojo> getConversationsByChoreography( ChoreographyPojo choreography );

    /**
     * @param partner
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    @SuppressWarnings("unchecked")
    public abstract List<MessagePojo> getMessagesByPartner( PartnerPojo partner, int field, boolean ascending )
            throws NexusException;

    /**
     * @param messagePojo
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public abstract void deleteMessage( MessagePojo messagePojo ) throws NexusException; // updateMessage

    /**
     * @param conversationPojo
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public abstract void deleteConversation( ConversationPojo conversationPojo ) throws NexusException; // updateMessage

    /**
     * @param partner
     * @param outbound
     * @param field
     * @param ascending
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    @SuppressWarnings("unchecked")
    public abstract List<MessagePojo> getMessagesByPartnerAndDirection( PartnerPojo partner, boolean outbound,
            int field, boolean ascending ) throws NexusException;

    @SuppressWarnings("unchecked")
    public abstract List<MessagePojo> getMessagesByActionPartnerDirectionAndStatus( ActionPojo action,
            PartnerPojo partner, boolean outbound, int status, int field, boolean ascending );

    /**
     * @param choreography
     * @param partner
     * @param field
     * @param ascending
     * @return
     */
    @SuppressWarnings("unchecked")
    public abstract List<MessagePojo> getMessagesByChoreographyAndPartner( ChoreographyPojo choreography,
            PartnerPojo partner, int field, boolean ascending );

    /**
     * @param choreography
     * @param partner
     * @param conversation
     * @param field
     * @param ascending
     * @return
     */
    @SuppressWarnings("unchecked")
    public abstract List<MessagePojo> getMessagesByChoreographyPartnerAndConversation( ChoreographyPojo choreography,
            PartnerPojo partner, ConversationPojo conversation, int field, boolean ascending );

    /**
     * @param logEntry
     * @param session
     * @param transaction
     */
    public abstract void deleteLogEntry( LogPojo logEntry ) throws NexusException;

    public abstract List<MessagePayloadPojo> fetchLazyPayloads( MessagePojo message );

    public abstract List<MessagePojo> fetchLazyMessages( ConversationPojo conversation );

    public abstract void updateTransaction( MessagePojo message, boolean force ) throws NexusException,
            StateTransitionException; // updateTransaction

    /**
     * Checks if the transition to the given status is allowed and returns it if so.
     * @param message The original message.
     * @param conversationStatus The target conversation status.
     * @return <code>conversationStatus</code> if transition is allowed, or the original
     * conversation status if not.
     */
    public abstract int getAllowedTransitionStatus( ConversationPojo conversation, int conversationStatus );

    /**
     * Checks if the transition to the given status is allowed and returns it if so.
     * @param message The original message.
     * @param messageStatus The target message status.
     * @return <code>messageStatus</code> if transition is allowed, or the original
     * message status if not.
     */
    public abstract int getAllowedTransitionStatus( MessagePojo message, int messageStatus );

    /**
     * Gets a count of messages that have been created since the given time. 
     * @param since The earliest creation date of messages that shall be counted.
     * @return A count.
     * @throws NexusException if something went wrong.
     */
    public abstract int getCreatedMessagesSinceCount( Date since ) throws NexusException;

}