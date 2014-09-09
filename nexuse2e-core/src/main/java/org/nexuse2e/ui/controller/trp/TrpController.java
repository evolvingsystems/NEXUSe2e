package org.nexuse2e.ui.controller.trp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.TRPPojo;
import org.nexuse2e.ui.form.TrpMaintenanceForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for TRP (Transport/Routing/Packaging) maintenance stuff.
 *
 * @author Jonas Reese
 */
@Controller
public class TrpController {

    @RequestMapping("/TrpMaintenance.do")
    public String trpMaintenance(
            Model model,
            @RequestParam(value = "submitAction", defaultValue = "") String submitAction,
            @Valid TrpMaintenanceForm form,
            BindingResult bindingResult,
            HttpServletRequest request,
            EngineConfiguration engineConfiguration) throws NexusException {

        if (submitAction.equals("add")) {
            if (StringUtils.isNotEmpty(form.getProtocol())
                    && StringUtils.isNotEmpty(form.getTransport())
                    && StringUtils.isNotEmpty(form.getVersion())) {
                TRPPojo trp = new TRPPojo();

                trp.setProtocol(form.getProtocol());
                trp.setTransport(form.getTransport());
                trp.setVersion(form.getVersion());
                trp.setAdapterClassName(form.getAdapterClassName());

                engineConfiguration.updateTrp(trp);
            }
        } else if (submitAction.equals("update")) {
            int nxId = form.getNxTRPId();
            if (nxId != 0) {
                TRPPojo trp = engineConfiguration.getTrpByNxTrpId(nxId);
                
                if (trp != null) {
                    trp.setProtocol(form.getProtocol());
                    trp.setTransport(form.getTransport());
                    trp.setVersion(form.getVersion());
                    trp.setAdapterClassName(form.getAdapterClassName());

                    engineConfiguration.updateTrp(trp);
                }
            }
        } else if (submitAction.equals("delete")) {
            int nxId = form.getNxTRPId();
            if (nxId != 0) {
                TRPPojo trp = engineConfiguration.getTrpByNxTrpId(nxId);
                if (trp != null) { 
                    engineConfiguration.deleteTrp(trp);
                }
            }
        }
        Comparator<TRPPojo> comparator = new Comparator<TRPPojo>() {
            public int compare(TRPPojo trp1, TRPPojo trp2) {
                if (trp1 == null) {
                    if (trp2 != null) {
                        return -1;
                    }
                    return 0;
                }
                if (trp2 == null) {
                    return 0;
                }
                String t1 = trp1.getTransport();
                String t2 = trp2.getTransport();
                if (t1 != null && t2 != null) {
                    int result = t1.compareTo( t2 );
                    if (result == 0) {
                        String p1 = trp1.getProtocol();
                        String p2 = trp2.getProtocol();
                        if (p1 != null && p2 != null) {
                            result = p1.compareTo( p2 );
                            if (result == 0) {
                                String v1 = trp1.getVersion();
                                String v2 = trp2.getVersion();
                                if (v1 != null && v2 != null) {
                                    result = v1.compareTo( v2 );
                                }
                            }
                        }
                    }
                    return result;
                }
                return 0;
            }
        };

        SortedSet<TRPPojo> set = new TreeSet<TRPPojo>(comparator);
        set.addAll(engineConfiguration.getTrps());

        List<TrpMaintenanceForm> collection = new ArrayList<TrpMaintenanceForm>(set.size());
        for (TRPPojo pojo : set) {
            collection.add(new TrpMaintenanceForm(pojo));
        }
        
        model.addAttribute("collection", collection);

        return "pages/trp/trp_maintenance";
    }
}
