/**
 *  NEXUSe2e Business Messaging Open Source
 *  Copyright 2000-2009, Tamgroup and X-ioma GmbH
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation version 2.1 of
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
package org.nexuse2e.ui.controller;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.nexuse2e.AdvancedControllerInterface;
import org.nexuse2e.Engine;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Home screen controller.
 *
 * @author Jonas Reese
 */
@Controller
public class HomeController {

    protected static Logger LOG = Logger.getLogger(HomeController.class);
    
    @RequestMapping("/NexusE2EAdmin.do")
    public String home(HttpSession session, Model model) {

        // Set version information to it is accessible by all JSPs
        model.addAttribute("java_version", System.getProperty("java.version"));
        model.addAttribute("java_classpath", System.getProperty("java.class.path"));
        model.addAttribute("java_home", System.getProperty( "java.home"));

        model.addAttribute("service_uptime", "n/d");
        model.addAttribute("engine_uptime", "n/d");

        try {
            long serviceUptime = System.currentTimeMillis() - Engine.getInstance().getServiceStartTime();
            long engineUptime = System.currentTimeMillis() - Engine.getInstance().getEngineStartTime();
        
            model.addAttribute("service_uptime", formatUptime(serviceUptime));
            model.addAttribute("engine_uptime", formatUptime(engineUptime));
        } catch (Exception e) {
            LOG.warn(e);
        }

        AdvancedControllerInterface aci = Engine.getInstance().getEngineController().getAdvancedController();
        if (aci != null) {
            model.addAttribute("instances", aci.getInstances());
            model.addAttribute("description", aci.getDescription());
        }

        return "admin.page";
    }

    @RequestMapping("/Home.do")
    public String home() {
        return "/WEB-INF/pages/home.jsp";
    }

    private String formatUptime( long serviceUptime ) {
        long dayLength = 1000 * 60 * 60 * 24;
        long hourlength = 1000 * 60 * 60;
        long minutelength = 1000 * 60;
        long secondlength = 1000;
         
        long days = serviceUptime / dayLength;
        long hours = (serviceUptime - (days * dayLength)) / hourlength;
        long minutes = (serviceUptime - (days * dayLength) - (hours * hourlength)) / minutelength;
        long seconds = (serviceUptime - (days * dayLength) - (hours * hourlength) - (minutes * minutelength)) / secondlength;

        return days + " days " + hours + " hours " + minutes + " minutes " + seconds + " seconds";
    }
}
