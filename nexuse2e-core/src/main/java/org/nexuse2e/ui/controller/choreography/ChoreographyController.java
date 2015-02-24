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
package org.nexuse2e.ui.controller.choreography;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.validation.Valid;

import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.GenericComparator;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.ui.form.ChoreographyForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Choreography maintenance controller.
 * 
 * @author Jonas Reese
 */
@Controller
public class ChoreographyController {
    @RequestMapping("/Choreographies.do")
    public String choreographies(Model model, EngineConfiguration engineConfiguration) throws NexusException {

        List<ChoreographyPojo> chorpojos = engineConfiguration.getChoreographies();
        List<ChoreographyForm> choreographies = new ArrayList<ChoreographyForm>();
        TreeSet<ChoreographyPojo> sortedChoreographies = new TreeSet<ChoreographyPojo>(new GenericComparator<ChoreographyPojo>("name", true));
        sortedChoreographies.addAll(chorpojos);
        for (ChoreographyPojo pojo : sortedChoreographies) {
            ChoreographyForm form = new ChoreographyForm();
            form.setProperties(pojo);
            choreographies.add(form);
        }

        model.addAttribute("collection", choreographies);

        return "pages/choreographies/choreographies";
    }

    @RequestMapping("/ChoreographyView.do")
    public String choreographyView(
            Model model, ChoreographyForm form, BindingResult result, EngineConfiguration engineConfiguration)
                    throws NexusException {

        ChoreographyPojo choreographyPojo = null;

        int nxChoreographyId = form.getNxChoreographyId();
        String choreographyId = form.getChoreographyName();

        if (nxChoreographyId != 0) {
            choreographyPojo = engineConfiguration.getChoreographyByNxChoreographyId(nxChoreographyId);
        } else if (choreographyId != null) {
            choreographyPojo = engineConfiguration.getChoreographyByChoreographyId(choreographyId);
        }

        if (choreographyPojo != null) {
            form.setActions(choreographyPojo.getActions());
            form.setParticipants(choreographyPojo.getParticipants());
            form.setProperties(choreographyPojo);
        }

        return "pages/choreographies/choreography_view";
    }

    @RequestMapping("/ChoreographyUpdate.do")
    public String choreographyUpdate(
            Model model, @Valid ChoreographyForm form, BindingResult result, EngineConfiguration engineConfiguration)
                    throws NexusException {
        
        if (result.hasErrors()) {
            return choreographyView(model, form, result, engineConfiguration);
        }

        ChoreographyPojo chor = engineConfiguration.getChoreographyByNxChoreographyId(form.getNxChoreographyId());
        if (chor != null) {
            chor.setDescription(form.getDescription());
            engineConfiguration.updateChoreography(chor);
        }
    
        return choreographies(model, engineConfiguration);
    }
}
