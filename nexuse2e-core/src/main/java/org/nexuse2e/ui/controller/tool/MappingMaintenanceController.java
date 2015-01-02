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
package org.nexuse2e.ui.controller.tool;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.nexuse2e.MappingType;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.MappingPojo;
import org.nexuse2e.ui.form.MappingMaintenanceForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Mapping maintenance controller.
 *
 * @author Jonas Reese
 */
@Controller
public class MappingMaintenanceController {

    public static final int RECORDS_PER_PAGE = 100;
    

    
    @RequestMapping("/MappingMaintenance.do")
    public String mappingMaintenance(
            MappingMaintenanceForm form, EngineConfiguration engineConfiguration, Model model)
                    throws NexusException {
        String action = form.getSubmitaction();
        form.setSubmitaction(null);

        System.out.println("values:"+MappingType.values());
        
        if (!StringUtils.isEmpty(action) && action.equals("add")) {
            if (!StringUtils.isEmpty(form.getCategory())) {
                MappingPojo mapping = new MappingPojo();

                mapping.setCategory(form.getCategory());
                mapping.setLeftType(form.getLeftType());
                mapping.setRightType(form.getRightType());
                
                mapping.setLeftValue(form.getLeftValue());
                mapping.setRightValue(form.getRightValue());

                engineConfiguration.updateMapping(mapping);
            }

        } else if (!StringUtils.isEmpty(action) && action.equals("update")) {
            if (!StringUtils.isEmpty(form.getCategory())) {
                int nxId = 0;
                try {
                    nxId = Integer.parseInt(form.getNxMappingId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (nxId != 0) {
                    MappingPojo mapping = engineConfiguration
                            .getMappingByNxMappingId(nxId);
                    
                    if (mapping != null) {
                        mapping.setCategory(form.getCategory());
                        mapping.setLeftType(form.getLeftType());
                        mapping.setRightType(form.getRightType());

                        mapping.setLeftValue(form.getLeftValue());
                        mapping.setRightValue(form.getRightValue());

                        engineConfiguration.updateMapping(mapping);
                    }
                }
            }
        } else if (!StringUtils.isEmpty(action) && action.equals("delete")) {
            if (!StringUtils.isEmpty(form.getCategory())) {
                int nxId = 0;
                try {
                    nxId = Integer.parseInt(form.getNxMappingId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (nxId != 0) {
                    MappingPojo mapping = engineConfiguration
                            .getMappingByNxMappingId(nxId);
                    if (mapping != null) { 
                        engineConfiguration.deleteMapping(mapping);
                    }
                }
            }
        }
        List<MappingPojo> list = engineConfiguration.getMappings(new Comparator<MappingPojo>() {
            public int compare(MappingPojo m1, MappingPojo m2) {
                String c1 = m1.getCategory();
                String c2 = m2.getCategory();
                if (c1 == null) {
                    c1 = "";
                }
                if (c2 == null) {
                    c2 = "";
                }
                int c = c1.compareTo(c2);
                if (c == 0) {
                    String l1 = m1.getLeftValue();
                    String l2 = m2.getLeftValue();
                    if (l1 == null) {
                        l1 = "";
                    }
                    if (l2 == null) {
                        l2 = "";
                    }
                    c = l1.compareTo(l2);
                }
                return c;
            }
        });
        form.setPageCount(list.size() / RECORDS_PER_PAGE + (list.size() % RECORDS_PER_PAGE == 0 ? 0 : 1));
        List<MappingPojo> mappings;
        if (list.size() > RECORDS_PER_PAGE) {
            mappings = new ArrayList<MappingPojo>(RECORDS_PER_PAGE);
            int startIndex = form.getCurrentPage() * RECORDS_PER_PAGE;
            for (int i = startIndex; i < startIndex + RECORDS_PER_PAGE && i < list.size(); i++) {
                mappings.add(list.get(i));
            }
        } else {
            mappings = list;
        }
        
        model.addAttribute("collection", mappings);

        List<String> typenames = new Vector<String>();
        for (MappingType type : MappingType.values()) {
            typenames.add(""+type);
        }
        
        form.setTypenames(typenames);

        return "pages/tools/mapping_maintenance";
    }
}
