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
package org.nexuse2e.backend.pipelets;

import java.util.Collections;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.messaging.MessageContext;

/**
 * @author gesch
 *
 */
public class DummyPipelet extends AbstractOutboundBackendPipelet {

    @SuppressWarnings("unused")
    private static Logger LOG = LogManager.getLogger( DummyPipelet.class );

    /**
     * Default constructor.
     */
    public DummyPipelet() {

        
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.backend.pipelets.AbstractOutboundBackendPipelet#processPayloadAvailable(org.nexuse2e.messaging.MessageContext)
     */
    @Override
    public MessageContext processPayloadAvailable( MessageContext messageContext ) throws NexusException {

        return messageContext;
    } 

    /* (non-Javadoc)
     * @see org.nexuse2e.backend.pipelets.AbstractOutboundBackendPipelet#processPrimaryKeyAvailable(org.nexuse2e.messaging.MessageContext)
     */
    @Override
    public MessageContext processPrimaryKeyAvailable( MessageContext messageContext ) throws NexusException {

        return messageContext;
    } 

    /* (non-Javadoc)
     * @see org.nexuse2e.Configurable#getParameterMap()
     */
    public Map<String, ParameterDescriptor> getParameterMap() {

        return Collections.unmodifiableMap( parameterMap );
    }

    
}
