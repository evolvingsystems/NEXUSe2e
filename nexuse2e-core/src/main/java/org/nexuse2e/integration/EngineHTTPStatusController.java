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
package org.nexuse2e.integration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.EngineMonitor;
import org.nexuse2e.EngineStatusSummary;
import org.nexuse2e.StatusSummary.Status;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author mbreilmann
 *
 */
public class EngineHTTPStatusController implements Controller {

    private static Logger LOG = LogManager.getLogger(EngineHTTPStatusController.class);

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax
     * .servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        try {
            EngineMonitor engineMonitor = Engine.getInstance().getEngineController().getEngineMonitor();
            EngineStatusSummary engineStatusSummary = engineMonitor.getStatus();

            if ((engineStatusSummary != null) && engineStatusSummary.getStatus().equals(Status.ACTIVE)) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().print("NEXUSe2e active");
                return null;
            }
        } catch (Exception e) {
            LOG.error("Error retrieving engine status: " + e);
            e.printStackTrace();
        }

        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);

        return null;
    }

}
