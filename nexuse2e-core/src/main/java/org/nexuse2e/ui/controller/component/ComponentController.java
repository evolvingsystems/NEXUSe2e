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
package org.nexuse2e.ui.controller.component;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.ComponentType;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.logging.LogAppender;
import org.nexuse2e.messaging.Pipelet;
import org.nexuse2e.pojo.ComponentPojo;
import org.nexuse2e.service.Service;
import org.nexuse2e.ui.form.ComponentForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Component maintenance controller.
 * 
 * @author Jonas Reese
 */
@Controller
public class ComponentController {

    protected static Logger LOG = Logger.getLogger(ComponentController.class);

    
    @RequestMapping("/Components.do")
    public String components(Model model, EngineConfiguration engineConfiguration) throws NexusException {

        List<ComponentForm> components = new ArrayList<ComponentForm>();
        List<ComponentPojo> componentPojos = null;
        componentPojos = engineConfiguration.getComponents(ComponentType.ALL, Constants.COMPONENT_NAME_COMPARATOR);

        for (ComponentPojo component : componentPojos) {
            ComponentForm cf = new ComponentForm();
            cf.setProperties( component );
            components.add( cf );
        }
        model.addAttribute("collection", components);
        
        return "pages/components/components";
    }
    
    @RequestMapping("/ComponentView.do")
    public String componentView(ComponentForm form, Model model, EngineConfiguration engineConfiguration) throws NexusException {

        ComponentPojo component = engineConfiguration.getComponentByNxComponentId(form.getNxComponentId());
        if (component != null) {
            form.setProperties(component);
        }

        return "pages/components/component_view";
    }
    
    @RequestMapping("/ComponentAdd.do")
    public String componentAdd(ComponentForm componentForm) {
        return "pages/components/component_view";
    }
    
    private ComponentPojo saveComponent(
            ComponentPojo component, BindingResult bindingResult, ComponentForm form, EngineConfiguration engineConfiguration)
                    throws NexusException {
        if (component == null) {
            component = new ComponentPojo();
        }
        
        int componentType = 0;
        try {
            Object o = Class.forName(form.getClassName()).newInstance();
            if (o instanceof Service) {
                componentType = ComponentType.SERVICE.getValue();
            } else if (o instanceof LogAppender) {
                componentType = ComponentType.LOGGER.getValue();
            } else if (o instanceof Pipelet) {
                componentType = ComponentType.PIPELET.getValue();
            } else {
                bindingResult.rejectValue(
                        "className", "component.error.invalidclass", new Object[] { form.getClassName() }, "Class not a valid component");
            }
        } catch (ClassNotFoundException cnfex) {
            LOG.error(cnfex);
            bindingResult.rejectValue(
                    "className", "component.error.classnotfound", new Object[] { form.getClassName() }, "Class not found");
        } catch (IllegalAccessException iaex) {
            LOG.error(iaex);
            bindingResult.rejectValue(
                    "className", "component.error.classnotaccessible", new Object[] { form.getClassName() }, "Class not accessible");
        } catch (InstantiationException iaex) {
            LOG.error(iaex);
            bindingResult.rejectValue(
                    "className", "component.error.classnotinstantiatable", new Object[] { form.getClassName() }, "Class not instantiatable");
        }
        
        if (!bindingResult.hasErrors()) {
            form.getProperties(component);
            engineConfiguration.updateComponent(component);
            component.setType(componentType);
        }
        return component;
    }
    
    @RequestMapping( { "/ComponentUpdate.do", "/ComponentCreate.do" })
    public String componentUpdate(
            Model model, @Valid ComponentForm form, BindingResult bindingResult, EngineConfiguration engineConfiguration)
                    throws NexusException {

        ComponentPojo component = engineConfiguration.getComponentByNxComponentId(form.getNxComponentId());
        component = saveComponent(component, bindingResult, form, engineConfiguration);
        
        form.setNxComponentId(component.getNxId());

        if (!bindingResult.hasErrors()) {
            return components(model, engineConfiguration);
        }

        return componentView(form, model, engineConfiguration);
    }
    
    @RequestMapping("/ComponentDelete.do")
    public String componentDelete(
            @RequestParam(value = "nxComponentId", required = true) int nxComponentId,
            Model model,
            EngineConfiguration engineConfiguration) throws NexusException {

        ComponentPojo component = engineConfiguration.getComponentByNxComponentId(nxComponentId);
        if (component != null) {
            engineConfiguration.deleteComponent(component);
        }

        return components(model, engineConfiguration);
    }
}
