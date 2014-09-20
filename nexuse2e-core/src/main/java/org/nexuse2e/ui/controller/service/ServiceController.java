package org.nexuse2e.ui.controller.service;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.GenericComparator;
import org.nexuse2e.pojo.ServicePojo;
import org.nexuse2e.ui.form.ServiceForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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
        TreeSet<ServicePojo> sortedServices = new TreeSet<ServicePojo>( new GenericComparator<ServicePojo>( "name", true ) );
        sortedServices.addAll( services );
        for ( ServicePojo service : sortedServices ) {
            ServiceForm serviceForm = new ServiceForm();
            serviceForm.setProperties( service );
            serviceForm.setServiceInstance( engineConfiguration.getService(
                    service.getName() ) );
            serviceList.add( serviceForm );
        }
        model.addAttribute("collection", serviceList);

        return "pages/services/service_list";
    }
}
