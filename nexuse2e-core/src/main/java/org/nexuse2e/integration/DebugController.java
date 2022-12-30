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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Debug incoming HTTP requests by dumping all parameters and the content to the log
 *
 * @author mbreilmann
 */
public class DebugController implements Controller {

    private static Logger LOG = LogManager.getLogger(DebugController.class);

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax
     * .servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String line = null;

        for (Object parameterName : request.getParameterMap().keySet()) {
            LOG.debug(parameterName + " = " + request.getParameter((String) parameterName));
        }

        InputStream inStream = request.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inStream));
        while ((line = bufferedReader.readLine()) != null) {
            LOG.debug(line);
        }

        response.setStatus(HttpServletResponse.SC_OK);

        return null;
    }

} // DebugController
