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
package org.nexuse2e.integration;

import java.rmi.RemoteException;

import jakarta.jws.Oneway;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;

/**
 * 
 * 
 * @author jonas.reese
 */
@WebService(name = "BackendDeliveryInterface", targetNamespace = "http://integration.nexuse2e.org/BackendDeliveryInterface/")
@SOAPBinding(use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface BackendDeliveryInterface {

    /**
     * Process an inbound message.
     * @param choreographyId The choreography ID.
     * @param businessPartnerId The partner ID.
     * @param actionId The action ID.
     * @param conversationId The conversation ID.
     * @param messageId The message ID.
     * @param payload The message payload.
     * @throws RemoteException If some processing error occurred.
     */
    @WebMethod(operationName = "processInboundMessage", action = "http://integration.nexuse2e.org/BackendDeliveryInterface/processInboundMessage")
    @WebResult(name = "statusResponse", targetNamespace = "")
    @Oneway
    public void processInboundMessage(
        @WebParam(name = "choreographyId", targetNamespace = "")
        String choreographyId,
        @WebParam(name = "businessPartnerId", targetNamespace = "")
        String businessPartnerId,
        @WebParam(name = "actionId", targetNamespace = "")
        String actionId,
        @WebParam(name = "conversationId", targetNamespace = "")
        String conversationId,
        @WebParam(name = "messageId", targetNamespace = "")
        String messageId,
        @WebParam(name = "payload", targetNamespace = "")
        String payload)
        throws RemoteException;

}
