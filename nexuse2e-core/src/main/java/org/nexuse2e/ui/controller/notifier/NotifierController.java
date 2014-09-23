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
package org.nexuse2e.ui.controller.notifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.ComponentType;
import org.nexuse2e.configuration.ConfigurationUtil;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.logging.LogAppender;
import org.nexuse2e.pojo.ComponentPojo;
import org.nexuse2e.pojo.LoggerParamPojo;
import org.nexuse2e.pojo.LoggerPojo;
import org.nexuse2e.ui.form.LoggerForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for notifier maintenance.
 *
 * @author Jonas Reese
 */
@Controller
public class NotifierController {
    
    @RequestMapping("/NotifierList.do")
    public String notifierList(Model model, EngineConfiguration engineConfiguration) {

        model.addAttribute("collection", engineConfiguration.getLoggers());
        return "pages/notifiers/notifier_list";
    }
    
    @RequestMapping("/NotifierView.do")
    public String notifierView(
            LoggerForm loggerForm,
            Model model,
            EngineConfiguration engineConfiguration) throws NexusException {

        LoggerPojo loggerPojo = engineConfiguration.getLoggerByNxLoggerId(loggerForm.getNxLoggerId());

        loggerForm.setProperties(loggerPojo);
        LogAppender logger = engineConfiguration
                .getLogger(loggerPojo.getName());
        for (LoggerParamPojo loggerParam : loggerPojo.getLoggerParams()) {
            loggerParam.setParameterDescriptor(logger.getParameterMap().get(loggerParam.getParamName()));
        }
        loggerForm.setParameters(ConfigurationUtil.getConfiguration(logger, loggerPojo));
        loggerForm.createParameterMapFromPojos();
        loggerForm.setLoggerInstance(logger);
        loggerForm.setComponentName(loggerPojo.getComponent().getName());

        Map<String, List<String>> logCategories = engineConfiguration.getLogCategories();

        loggerForm.getGroupNames().clear();
        HashMap<String, String> tempFilterValues = new HashMap<String, String>();
        HashMap<String, String> enabledFilterValues = new HashMap<String, String>();
        StringTokenizer st = new StringTokenizer(loggerPojo.getFilter(), ",");
        while (st.hasMoreTokens()) {
            enabledFilterValues.put(st.nextToken(), "true");
        }

        loggerForm.setThreshold(loggerPojo.getThreshold());

        for (String group : logCategories.keySet()) {

            String value = enabledFilterValues.get("group_" + group);
            if (value != null) {
                tempFilterValues.put("group_" + group, value);
                enabledFilterValues.remove("group_" + group);
            } else {
                tempFilterValues.put("group_" + group, "false");
            }
            loggerForm.getGroupNames().add(group);
        }
        if (enabledFilterValues.size() > 0) {
            StringBuffer filterBuffer = new StringBuffer();
            for (String pattern : enabledFilterValues.keySet()) {
                if (pattern.trim().equals("")) {
                    continue;
                }
                filterBuffer.append(pattern.trim());
                filterBuffer.append(", ");
            }
            loggerForm.setFilterJavaPackagePattern(filterBuffer.toString());
        }

        List<ComponentPojo> components = engineConfiguration.getComponents(ComponentType.LOGGER, Constants.COMPONENTCOMPARATOR);
        model.addAttribute("collection", components);
        
        return "pages/notifiers/notifier_view";
    }
}
