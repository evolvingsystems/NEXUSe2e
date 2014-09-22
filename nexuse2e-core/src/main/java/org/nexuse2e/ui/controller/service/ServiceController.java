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
package org.nexuse2e.ui.controller.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import javax.validation.Valid;

import org.nexuse2e.BeanStatus;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.ConfigurationUtil;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.GenericComparator;
import org.nexuse2e.pojo.ComponentPojo;
import org.nexuse2e.pojo.ServiceParamPojo;
import org.nexuse2e.pojo.ServicePojo;
import org.nexuse2e.service.Service;
import org.nexuse2e.ui.form.ServiceForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for service maintenance.
 *
 * @author Jonas Reese
 */
@Controller
public class ServiceController {
    
    @RequestMapping("/ServiceList.do")
    public String serviceList(Model model, EngineConfiguration engineConfiguration) {

        List<ServicePojo> services = engineConfiguration.getServices();
        List<ServiceForm> serviceList = new ArrayList<ServiceForm>();
        TreeSet<ServicePojo> sortedServices = new TreeSet<ServicePojo>(new GenericComparator<ServicePojo>("name", true));
        sortedServices.addAll(services);
        for (ServicePojo service : sortedServices) {
            ServiceForm serviceForm = new ServiceForm();
            serviceForm.setProperties(service);
            serviceForm.setServiceInstance(engineConfiguration.getService(service.getName()));
            serviceList.add( serviceForm );
        }
        model.addAttribute("collection", serviceList);

        return "pages/services/service_list";
    }
    
    @RequestMapping("/ServiceView.do")
    public String serviceView(ServiceForm serviceForm, BindingResult bindingResult, Model model, EngineConfiguration engineConfiguration)
            throws NexusException {
        
        ServicePojo servicePojo = engineConfiguration.getServicePojoByNxServiceId(serviceForm.getNxServiceId());
        serviceForm.setProperties(servicePojo);

        if (servicePojo != null) {
            try {
                Service service = engineConfiguration.getServiceInstanceFromPojo(servicePojo);
                for (ServiceParamPojo serviceParam : servicePojo.getServiceParams()) {
                    serviceParam.setParameterDescriptor(service.getParameterMap().get(serviceParam.getParamName()));
                }
                serviceForm.setParameters(ConfigurationUtil.getConfiguration(service, servicePojo));
                serviceForm.createParameterMapFromPojos();
                serviceForm.setServiceInstance(service);
            } catch (NexusException ex) {
                if (ex.getCause() instanceof ClassNotFoundException) {
                    String className = servicePojo.getComponent() != null ? servicePojo.getComponent().getClassName() : "<unknown>";
                    bindingResult.reject("error.component.classnotfound", new Object[] { className }, "Component class " + className + " not found");
                } else {
                    throw ex;
                }
            }
            
            List<ServicePojo> services = engineConfiguration.getServices();
            List<ServicePojo> sortedServices = new ArrayList<ServicePojo>(services.size());
            sortedServices.addAll(engineConfiguration.getServices());
            Collections.sort(sortedServices, new GenericComparator<ServicePojo>("name", true));
            Service serviceInstance = engineConfiguration.getService(servicePojo.getName());
            model.addAttribute("serviceStatus", serviceInstance == null ? BeanStatus.UNDEFINED : serviceInstance.getStatus());
            model.addAttribute("services", sortedServices);
        }
        
        return "pages/services/service_view";
    }

    @RequestMapping("/ServiceStart.do")
    public String serviceStart(
            Model model,
            ServiceForm serviceForm,
            BindingResult bindingResult,
            @RequestParam(value = "viewService", defaultValue = "false") boolean viewService,
            EngineConfiguration engineConfiguration)
                    throws NexusException {

        ServicePojo servicePojo = engineConfiguration.getServicePojoByNxServiceId(serviceForm.getNxServiceId());
        if (servicePojo != null) {
            Service service = engineConfiguration.getService(servicePojo.getName());
            if (service != null && service.getStatus() == BeanStatus.ACTIVATED) {
                service.start();
            }
        }

        if (viewService) {
            return serviceView(serviceForm, bindingResult, model, engineConfiguration);
        }
        return serviceList(model, engineConfiguration);
    }

    @RequestMapping("/ServiceStop.do")
    public String serviceStop(
            Model model,
            @Valid ServiceForm serviceForm,
            BindingResult bindingResult,
            @RequestParam(value = "viewService", defaultValue = "false") boolean viewService,
            EngineConfiguration engineConfiguration)
                    throws NexusException {

        ServicePojo servicePojo = engineConfiguration.getServicePojoByNxServiceId(serviceForm.getNxServiceId());
        if (servicePojo != null) {
            Service service = engineConfiguration.getService(servicePojo.getName());
            if (service != null && service.getStatus() == BeanStatus.STARTED) {
                service.stop();
            }
        }

        if (viewService) {
            return serviceView(serviceForm, bindingResult, model, engineConfiguration);
        }
        return serviceList(model, engineConfiguration);
    }

    @RequestMapping("/ServiceUpdate.do")
    public String serviceUpdate(ServiceForm serviceForm, BindingResult bindingResult, Model model, EngineConfiguration engineConfiguration)
            throws NexusException {

        ServicePojo originalService = engineConfiguration.getServicePojoByNxServiceId(serviceForm.getNxServiceId());

        ComponentPojo component = null;

        if (!bindingResult.hasErrors()) {
            if (originalService != null) {
                if (originalService != null) {
                    component = originalService.getComponent();
                }
            }
            if (component == null) {
                component = engineConfiguration.getComponentByNxComponentId(serviceForm.getNxComponentId());
            }
    
            if (component != null && serviceForm.getSubmitted() != null && serviceForm.getSubmitted().equals("true") ) {
                serviceForm.setSubmitted("false");
    
                if (originalService == null) {
                    originalService = new ServicePojo();
                    originalService.setName(serviceForm.getName());
                    originalService.setComponent(component);
                }
    
                originalService.setAutostart(serviceForm.isAutostart());
                
                serviceForm.fillPojosFromParameterMap();
                List<ServiceParamPojo> list = serviceForm.getParameters();
                for (ServiceParamPojo param : list) {
                    param.setService(originalService);
                }
    
                if (!originalService.getName().equals(serviceForm.getName())) {
                    engineConfiguration.renameService(originalService.getName(), serviceForm.getName());
                    originalService.setName(serviceForm.getName());
                }
                originalService.setServiceParams(list);
    
                engineConfiguration.updateService(originalService);
                ConfigurationUtil.configureService(serviceForm.getServiceInstance(), list);
            }

            return serviceList(model, engineConfiguration);
        } else {
            List<ServicePojo> services = engineConfiguration.getServices();
            List<ServicePojo> sortedServices = new ArrayList<ServicePojo>(services.size());
            sortedServices.addAll(engineConfiguration.getServices());
            Collections.sort(sortedServices, new GenericComparator<ServicePojo>("name", true));
            Service serviceInstance = (originalService != null ? engineConfiguration.getService(originalService.getName()) : null);
            model.addAttribute("serviceStatus", serviceInstance == null ? BeanStatus.UNDEFINED : serviceInstance.getStatus());
            model.addAttribute("services", sortedServices);

            return "pages/services/service_view";
        }
    }
}
