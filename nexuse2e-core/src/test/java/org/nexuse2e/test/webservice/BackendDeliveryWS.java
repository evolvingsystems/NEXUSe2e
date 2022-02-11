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
package org.nexuse2e.test.webservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nexuse2e.integration.BackendDeliveryInterface;

import javax.jws.WebService;

/**
 * @author jonas.reese
 * @version $LastChangedRevision$ - $LastChangedDate$ by $LastChangedBy$
 */
@WebService(endpointInterface = "org.nexuse2e.integration.BackendDeliveryInterface")
public class BackendDeliveryWS implements BackendDeliveryInterface {

    private static Logger LOG = LogManager.getLogger(BackendDeliveryWS.class);

    /* (non-Javadoc)
     * @see org.nexuse2e.integration.BackendDeliveryInterface#processInboundMessage(java.lang.String, java.lang
     * .String, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
     */
    public void processInboundMessage(String choreographyId, String businessPartnerId, String actionId,
                                      String conversationId, String messageId, String payload) {
        LOG.info("choreographyId=" + choreographyId + " businessPartnerId=" + businessPartnerId + " actionId=" + actionId + " conversationId=" + conversationId + " messageId=" + messageId + " payload=" + payload);
        return;
    }

    
/*
 * Add this to the NexusWebServiceDispatcher-servlet.xml bean configuration:
 * 
    <!-- BEGIN TESTING SECTION -->
    <bean id="xfire.annotServiceFactory"
      class="org.codehaus.xfire.annotations.AnnotationServiceFactory"
      singleton="true">
    </bean>
    <bean id="BackendDeliveryInterface" class="org.codehaus.xfire.spring.remoting.XFireExporter">
        <property name="serviceFactory">
            <ref bean="xfire.annotServiceFactory"/>
        </property>
        <property name="xfire">
            <ref bean="xfire"/>
        </property>
        <property name="serviceBean">
            <bean class="org.nexuse2e.test.webservice.BackendDeliveryWS"/>
        </property>
        <property name="serviceClass">
            <value>org.nexuse2e.integration.BackendDeliveryInterface</value>
        </property>
    </bean>
    <!-- END TESTING SECTION -->
    
 *
 * Add this to the URL handler mapping 
 * 
    <entry key="/BackendDeliveryInterface">
        <ref bean="BackendDeliveryInterface"/>
    </entry>
 * 
 * For client testing against the output logger, add this to URL handler mapping:
 * 
    <entry key="/RequestLogger">
        <bean class="org.nexuse2e.test.RequestLogger"/>
    </entry>
 */
}
