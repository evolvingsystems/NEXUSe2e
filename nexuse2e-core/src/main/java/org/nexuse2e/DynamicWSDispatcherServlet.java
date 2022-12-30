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
package org.nexuse2e;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nexuse2e.logging.LogMessage;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;

/**
 * This subclass of <code>CXFNonSpringServlet</code> keeps a reference on the instance
 * created by the servlet container in order to make it available for code that
 * dynamically registers/unregisters web services.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class DynamicWSDispatcherServlet extends CXFServlet {
    private static final long serialVersionUID = 1L;
    private static Logger LOG = LogManager.getLogger(DynamicWSDispatcherServlet.class);
    private static DynamicWSDispatcherServlet instance;

    private ServletConfig servletConfig;

    public DynamicWSDispatcherServlet() {
        instance = this;
    }

    /**
     * Gets the most recently created instance of this class.
     *
     * @return The last created instance, or <code>null</code> if none was created yet.
     */
    public static DynamicWSDispatcherServlet getInstance() {
        return instance;
    }

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        this.servletConfig = servletConfig;
    }

    public void reinitialize() {
        try {
            super.init(servletConfig);
        } catch (ServletException e) {
            LOG.error(new LogMessage("failed to reinitialize WS Dispatcher service.", e));
        }
    }
}
