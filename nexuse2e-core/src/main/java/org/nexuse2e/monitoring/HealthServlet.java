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
package org.nexuse2e.monitoring;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONStringer;
import org.nexuse2e.Engine;
import org.nexuse2e.EngineStatusSummary;
import org.nexuse2e.NexusException;
import org.nexuse2e.StatusSummary;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

/**
 * @author Guido Esch
 */
public class HealthServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        Writer writer = response.getWriter();
        try {
            String healthJSON = new JSONStringer().object()
                    .key("healthStatus").value(false).endObject().toString();

            if (Engine.getInstance().getEngineController() != null && Engine.getInstance().getEngineController().getEngineMonitor() != null) {
                EngineStatusSummary summary = Engine.getInstance().getEngineController().getEngineMonitor().getStatus();
                if (summary != null) {
                    String summaryStatus = "unknown";
                    switch(summary.getStatus()) {
                        case ACTIVE:
                            summaryStatus = "active";
                            break;
                        case ERROR:
                            summaryStatus = "down";
                            break;
                        case UNKNOWN:
                            summaryStatus = "down";
                            break;
                        case INACTIVE:
                            summaryStatus = "maintenance";
                            break;
                    }
                    healthJSON = new JSONStringer().object()
                            .key("healthStatus").value(summaryStatus)
                            .key("databases").array()
                            .object().key("default").value(summary.getDatabaseStatus().equals(StatusSummary.Status.ACTIVE)).endObject()
                            .endArray()
                            .key("services").array()
                            .endArray()
                            .key("details").array()
                            .endArray()
                            .endObject().toString();
                }
            }
            writer.write(healthJSON);
        } catch (NexusException | JSONException ignored) {
        }
        writer.flush();
    }
}
