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

import java.util.List;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.ComponentType;
import org.nexuse2e.configuration.ConfigurationUtil;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.logging.LogAppender;
import org.nexuse2e.pojo.ComponentPojo;
import org.nexuse2e.pojo.LoggerPojo;
import org.nexuse2e.ui.controller.service.ServiceController;
import org.nexuse2e.ui.form.LoggerForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for notifier maintenance.
 *
 * @author Jonas Reese
 */
@Controller
public class NotifierController {
    
    protected static Logger LOG = Logger.getLogger(ServiceController.class);
    
    @RequestMapping("/NotifierList.do")
    public String notifierList(Model model, EngineConfiguration engineConfiguration) {

        model.addAttribute("collection", engineConfiguration.getLoggers());
        return "pages/notifiers/notifier_list";
    }
    
    @RequestMapping("/NotifierView.do")
    public String notifierView(
            LoggerForm loggerForm,
            BindingResult bindingResult,
            Model model,
            EngineConfiguration engineConfiguration) throws NexusException {

        LoggerPojo loggerPojo = engineConfiguration.getLoggerByNxLoggerId(loggerForm.getNxLoggerId());
        if (loggerPojo == null) {
            return notifierList(model, engineConfiguration);
        }
        loggerForm.setLogger(loggerPojo);
        loggerForm.setProperties(loggerPojo);
        loggerForm.setParameters(loggerPojo.getLoggerParams());
        loggerForm.createParameterMapFromPojos();

        model.addAttribute("collection", engineConfiguration.getComponents(ComponentType.LOGGER, Constants.COMPONENTCOMPARATOR));
        model.addAttribute("services", engineConfiguration.getServices());
        
        return "pages/notifiers/notifier_view";
    }
    
    private void fillLoggerInstance(LoggerForm loggerForm, ComponentPojo componentPojo, BindingResult bindingResult) {
        if (componentPojo != null) {
            LogAppender logger = null;
            String className = componentPojo.getClassName();
            try {
                Object obj = Class.forName(className).newInstance();
                if (obj instanceof LogAppender) {
                    logger = (LogAppender) obj;
                    loggerForm.setLoggerInstance(logger);
                    loggerForm.setParameters(ConfigurationUtil.getConfiguration(logger, loggerForm.getLogger()));
                }
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                LOG.error(e);
            }
            if (loggerForm.getLoggerInstance() == null) {
                bindingResult.reject("error.component.classnotfound", new Object[] { className }, "Component class " + className + " not found");
            }
        }
    }

    @RequestMapping("/NotifierAdd.do")
    public String notifierAdd(
            LoggerForm loggerForm,
            BindingResult bindingResult,
            Model model,
            EngineConfiguration engineConfiguration) throws NexusException {

        List<ComponentPojo> components = engineConfiguration.getComponents(ComponentType.LOGGER, Constants.COMPONENTCOMPARATOR);
        
        if (components == null || components.isEmpty()) {
            return notifierList(model, engineConfiguration);
        }

        ComponentPojo componentPojo = null;
        if (loggerForm.getNxComponentId() == 0) {
            componentPojo = components.iterator().next();
        } else {
            componentPojo = engineConfiguration.getComponentByNxComponentId(loggerForm.getNxComponentId());
        }

        loggerForm.setLogger(new LoggerPojo());
        fillLoggerInstance(loggerForm, componentPojo, bindingResult);
        loggerForm.createParameterMapFromPojos();

        model.addAttribute("collection", components);
        model.addAttribute("services", engineConfiguration.getServices());

        return "pages/notifiers/notifier_view";
    }
    
    @RequestMapping("/NotifierUpdate.do")
    public String notifierUpdate(
            @Valid LoggerForm loggerForm,
            BindingResult bindingResult,
            Model model,
            EngineConfiguration engineConfiguration) throws NexusException {

        ComponentPojo componentPojo = engineConfiguration.getComponentByNxComponentId(loggerForm.getNxComponentId());

        if (componentPojo == null) {
            bindingResult.rejectValue(
                    "nxComponentId",
                    "component.error.notfound", new Object[]{ ComponentType.LOGGER, loggerForm.getNxComponentId() },
                    "Component not found");
        }
        
        LoggerPojo originalLogger = engineConfiguration.getLoggerByNxLoggerId(loggerForm.getNxLoggerId());
        if (originalLogger == null) {
            originalLogger = new LoggerPojo();
        }
        loggerForm.setLogger(originalLogger);
        fillLoggerInstance(loggerForm, componentPojo, bindingResult);
        loggerForm.fillPojosFromParameterMap();

        if (!bindingResult.hasErrors()) {
            originalLogger.setName(loggerForm.getName());
            originalLogger.setComponent(componentPojo);
            originalLogger.setFilter(loggerForm.getFilter());
            originalLogger.setLoggerParams(loggerForm.getParameters());
    
            if (!originalLogger.getName().equals(loggerForm.getName())) {
                engineConfiguration.renameLogger(originalLogger.getName(), loggerForm.getName());
                originalLogger.setName(loggerForm.getName());
            }
            originalLogger.setThreshold(loggerForm.getThreshold());
            engineConfiguration.updateLogger(originalLogger);
        }

        if (bindingResult.hasErrors()) {
            loggerForm.setNxComponentId(componentPojo == null ? 0 : componentPojo.getNxComponentId());
            loggerForm.setComponentName(componentPojo == null ? null : componentPojo.getName());
            
            model.addAttribute("collection", engineConfiguration.getComponents(ComponentType.LOGGER, Constants.COMPONENTCOMPARATOR));
            model.addAttribute("services", engineConfiguration.getServices());

            return "pages/notifiers/notifier_view";
        }
        return notifierList(model, engineConfiguration);
    }
    
    @RequestMapping("/NotifierDelete.do")
    public String notifierDelete(
            @RequestParam("nxLoggerId") int nxLoggerId, Model model, EngineConfiguration engineConfiguration)
                    throws NexusException {

        LoggerPojo logger = engineConfiguration.getLoggerByNxLoggerId(nxLoggerId);
        if (logger != null) {
            engineConfiguration.deleteLogger(logger);
        }

        return "redirect:/NotifierList.do";
    }
}
