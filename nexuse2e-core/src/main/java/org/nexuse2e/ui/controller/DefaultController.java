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

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.AdvancedControllerInterface;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.ui.ajax.dojo.TreeProvider;
import org.nexuse2e.ui.structure.StructureException;
import org.nexuse2e.ui.structure.StructureNode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Home screen controller.
 *
 * @author Jonas Reese
 */
@Controller
public class DefaultController {

    protected static Logger LOG = Logger.getLogger(DefaultController.class);
    
    private static String formatUptime( long serviceUptime ) {
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

    private static void fillHomeModel(Model model) {
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
    }


    @RequestMapping("/NexusE2EAdmin.do")
    public String nexuse2eAdmin(Model model) {
        fillHomeModel(model);
        return "admin.page";
    }

    @RequestMapping("/Home.do")
    public String home(Model model) {
        fillHomeModel(model);
        return "pages/home";
    }
    
    @RequestMapping("/InstanceController.do")
    public String instanceController(@RequestParam("instanceId") String instanceId, @RequestParam("commandId") String commandId) {

        if (StringUtils.isNotEmpty(instanceId) && StringUtils.isNotEmpty(commandId)) {
            Engine.getInstance().getEngineController().getAdvancedController().executeCommand(instanceId, commandId);
        }
        return "redirect:/Home.do";
    }
    
    @RequestMapping({
        "/ServerConfiguration.do",
        "/UserMaintenance.do",
        "/Certificates.do",
        "/Tools.do"
        })
    public String childPages(Model model, HttpServletRequest request, EngineConfiguration engineConfiguration) throws StructureException {

        String parentId = null;
        int index = request.getRequestURI().lastIndexOf('/');
        if (index >= 0) {
            parentId = request.getRequestURI().substring(index + 1, request.getRequestURI().length());
        }
        if (parentId == null) {
            throw new IllegalArgumentException("Could not extract parent ID from request.");
        }
        
        TreeProvider provider = new TreeProvider();
        
        List<StructureNode> nodes = provider.getParentAndChildNodes(parentId, engineConfiguration);
        
        model.addAttribute("parent", nodes.isEmpty() ? null : nodes.get(0));
        model.addAttribute("children", nodes.isEmpty() ? null : nodes.subList(1, nodes.size()));
        
        return "pages/show_childpages";
    }
}
