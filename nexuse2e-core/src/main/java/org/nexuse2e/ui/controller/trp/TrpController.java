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
package org.nexuse2e.ui.controller.trp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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

    private void addToTrpModel(Model model, List<TRPPojo> trps, int updateNxId, TrpMaintenanceForm form, BindingResult bindingResult) {
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
        set.addAll(trps);

        List<TrpMaintenanceForm> collection = new ArrayList<TrpMaintenanceForm>(set.size());
        int i = 0;
        for (TRPPojo pojo : set) {
            TrpMaintenanceForm f;
            if (updateNxId != 0 && form != null && pojo.getNxId() == updateNxId) {
                f = form;
                model.addAttribute("trpMaintenanceForm", new TrpMaintenanceForm());
                model.addAttribute("org.springframework.validation.BindingResult.trpMaintenanceForm", null);
                model.addAttribute("org.springframework.validation.BindingResult.collection_" + i, bindingResult);
            } else {
                f = new TrpMaintenanceForm(pojo);
            }
            collection.add(f);
            model.addAttribute("collection_" + i, f);
            i++;
        }
        
        model.addAttribute("collection", collection);
    }
    
    @RequestMapping("/TrpMaintenance.do")
    public String trpMaintenance(
            Model model,
            @RequestParam(value = "submitAction", defaultValue = "") String submitAction,
            @Valid TrpMaintenanceForm form,
            BindingResult bindingResult,
            HttpServletRequest request,
            EngineConfiguration engineConfiguration) throws NexusException {

        int nxId = form.getNxTRPId();
        if (submitAction.equals("add")) {
            if (!bindingResult.hasErrors()) {
                TRPPojo trp = new TRPPojo();

                trp.setProtocol(form.getProtocol());
                trp.setTransport(form.getTransport());
                trp.setVersion(form.getVersion());
                trp.setAdapterClassName(form.getAdapterClassName());

                engineConfiguration.updateTrp(trp);
            }
        } else if (submitAction.equals("update")) {
            if (!bindingResult.hasErrors()) {
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
            }
        } else if (submitAction.equals("delete")) {
            if (nxId != 0) {
                TRPPojo trp = engineConfiguration.getTrpByNxTrpId(nxId);
                if (trp != null) { 
                    engineConfiguration.deleteTrp(trp);
                }
            }
            nxId = 0;
        }
        
        addToTrpModel(model, engineConfiguration.getTrps(), nxId, form, bindingResult);

        return "pages/trp/trp_maintenance";
    }
}
