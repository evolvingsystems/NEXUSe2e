/**
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
package org.nexuse2e;


import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.MessageProcessor;
import org.nexuse2e.util.NexusThreadStorage;

import jakarta.xml.ws.WebFault;


@WebFault(name = "NexusException", targetNamespace = "http://integration.nexuse2e.org")
public class NexusException extends Exception {

    private int    severity;
    private String messageId;
    private String choreographyId;
    private String partnerId;
    private String conversationId;
    private String transportId;
    private String protocolId;
    private String protocolVersion;

    /**
     *
     */
    private static final long serialVersionUID = 1701877587184448016L;

    /**
     * @param message
     */
    public NexusException(String message) {

        super(message);
    }

    /**
     * @param nested exception
     */
    public NexusException(Exception nested) {

        super(nested);
    }

    public NexusException(String message, Exception nested) {

        super(message, nested);
    }

    /**
     * Initializes the exception from a LogMessage instance.
     *
     * @param logMessage The LogMessage that carries the information for this exception.
     */
    public NexusException(LogMessage logMessage) {
        super(logMessage.toString(false));
        setInfoFromLogMessage(logMessage);
    }

    /**
     * Initializes the exception from a LogMessage instance.
     *
     * @param logMessage The LogMessage that carries the information for this exception.
     * @param nested     The nested cause of this exception.
     */
    public NexusException(LogMessage logMessage, Exception nested) {
        super(logMessage.toString(false), nested);
        setInfoFromLogMessage(logMessage);
    }

    // attempts to pre-fill values for message ID and conversation ID from thread-local values
    {
        Object conversationId = NexusThreadStorage.get("conversationId");
        Object messageId = NexusThreadStorage.get("messageId");
        if (null != conversationId && conversationId instanceof String) {
            this.conversationId = (String) conversationId;
        }
        if (null != messageId && messageId instanceof String) {
            this.messageId = (String) messageId;
        }
    }

    /**
     * Initializes the <code>conversationId</code> and <code>messageId</code>
     * fields of this instance from the given <code>logMessage</code>.
     *
     * @param logMessage The LogMessage that carries the information for this exception.
     */
    private void setInfoFromLogMessage(LogMessage logMessage) {
        setConversationId(logMessage.getConversationId());
        setMessageId(logMessage.getMessageId());
    }

    public String getConversationDetails() {

        return null;
    }

    /**
     * @return the choreographyId
     */
    public String getChoreographyId() {

        return choreographyId;
    }

    /**
     * @param choreographyId the choreographyId to set
     */
    public void setChoreographyId(String choreographyId) {

        this.choreographyId = choreographyId;
    }

    /**
     * @return the conversationId
     */
    public String getConversationId() {

        return conversationId;
    }

    /**
     * @param conversationId the conversationId to set
     */
    public void setConversationId(String conversationId) {

        this.conversationId = conversationId;
    }

    /**
     * @return the messageId
     */
    public String getMessageId() {

        return messageId;
    }

    /**
     * @param messageId the messageId to set
     */
    public void setMessageId(String messageId) {

        this.messageId = messageId;
    }

    /**
     * @return the partnerId
     */
    public String getPartnerId() {

        return partnerId;
    }

    /**
     * @param partnerId the partnerId to set
     */
    public void setPartnerId(String partnerId) {

        this.partnerId = partnerId;
    }

    /**
     * @return the severity
     */
    public int getSeverity() {

        return severity;
    }

    /**
     * @param severity the severity to set
     */
    public void setSeverity(int severity) {

        this.severity = severity;
    }

    /**
     * @return the protocolId
     */
    public String getProtocolId() {

        return protocolId;
    }

    /**
     * @param protocolId the protocolId to set
     */
    public void setProtocolId(String protocolId) {

        this.protocolId = protocolId;
    }

    /**
     * @return the protocolVersion
     */
    public String getProtocolVersion() {

        return protocolVersion;
    }

    /**
     * @param protocolVersion the protocolVersion to set
     */
    public void setProtocolVersion(String protocolVersion) {

        this.protocolVersion = protocolVersion;
    }

    /**
     * @return the transportId
     */
    public String getTransportId() {

        return transportId;
    }

    /**
     * @param transportId the transportId to set
     */
    public void setTransportId(String transportId) {

        this.transportId = transportId;
    }

    /* (non-Javadoc)
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        return getMessage(true);
    }

    /**
     * @param full
     * @return
     */
    public String getMessage(boolean full) {
        if (full) {
            return conversationId + "/" + messageId + ": " + super.getMessage();
        } else {
            return super.getMessage();
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Throwable#toString()
     */
    @Override
    public String toString() {
        return toString(true);
    }

    /**
     * @param full
     * @return
     */
    public String toString(boolean full) {
        String s = getClass().getName();
        String message = super.getLocalizedMessage();
        message = (message != null) ? (s + ": " + message) : s;
        if (full) {
            return conversationId + "/" + messageId + ": " + message;
        } else {
            return message;
        }
    }

}
