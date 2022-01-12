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
package org.nexuse2e.messaging;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nexuse2e.BeanStatus;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.ParameterType;
import org.nexuse2e.logging.LogMessage;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

/**
 * @author sschulze
 */
public class AbstractGroovyPipelet extends AbstractPipelet {

    public static final String GROOVY_CODE = "groovyCode";

    private static final Logger LOG = LogManager.getLogger(AbstractGroovyPipelet.class);

    protected String groovyCode;

    public AbstractGroovyPipelet() {
        super();
        parameterMap.put(GROOVY_CODE, new ParameterDescriptor(ParameterType.TEXT, "Groovy Code", "The Groovy source " +
				"code that should be used for processing a message.", ""));
    }

    @Override
    public void initialize(EngineConfiguration config) throws InstantiationException {
        if (LOG.isDebugEnabled()) {
            LOG.trace("Initializing");
        }

        groovyCode = getParameter(GROOVY_CODE);
        if (StringUtils.isNotEmpty(groovyCode)) {
            status = BeanStatus.INITIALIZED;
        } else {
            groovyCode = null; // to avoid empty string
            LOG.warn("No Groovy code provided. " + "The pipelet will just forward the message context without " +
					"processing.");
        }

        if (LOG.isDebugEnabled()) {
            LOG.trace("Initialization done");
        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    @Override
    public MessageContext processMessage(MessageContext messageContext) throws IllegalArgumentException,
			IllegalStateException, NexusException {

        if (StringUtils.isNotEmpty(groovyCode)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Invoking Groovy code");
            }
            try {
                Binding binding = new Binding();
                binding.setVariable("messageContext", messageContext);
                GroovyShell shell = new GroovyShell(this.getClass().getClassLoader(), binding);
                Object result = shell.evaluate(groovyCode);
                if (result != null) {
                    if (result instanceof MessageContext) {
                        return (MessageContext) result;
                    } else {
                        throw new NexusException("Result of Groovy code must be of type org.nexuse2e.messaging" +
								".MessageContext. " + "The type of the returned object is " + result.getClass().getName());
                    }
                } else {
                    return messageContext;
                    //throw new NexusException( "Result of Groovy code must not be null, but an instance of org
					// .nexuse2e.messaging.MessageContext" );
                }
            } catch (Exception e) {
                throw new NexusException(new LogMessage("Error invoking Groovy code: " + e.getMessage(), messageContext), e);
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("No Groovy code for processMessage defined");
            }
            return messageContext;
        }
    }

}
