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

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;

/**
 * This interface defines utility functionality that can be used by remote clients
 * for inquiry.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
@WebService(name = "NEXUSe2eUtilities", targetNamespace = "http://integration.nexuse2e.org/NEXUSe2eUtilities/")
@SOAPBinding(use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface NEXUSe2eUtilities {

    /**
     * Returns <code>true</code> if and only if the current configuration or the specified choreography
     * contains a partner with the given partner ID.
     * @param partnerId The partner ID to check. Must not be <code>null</code>.
     * @param choreographyName The name of the choreography to search the partner in. If <code>null</code>,
     * the whole configuration is checked for the given partner ID. 
     * @return <code>true</code> if the partner is configured (in the given choreography),
     * <code>false</code> otherwise. 
     */
    @WebMethod(operationName = "containsPartner", action = "http://integration.nexuse2e.org/NEXUSe2eUtilities/containsPartner")
    @WebResult(name = "containsPartnerResponse")
    public boolean containsPartner(
            @WebParam(name = "partnerId") String partnerId,
            @WebParam(name = "choreographyName") String choreographyName );
}
