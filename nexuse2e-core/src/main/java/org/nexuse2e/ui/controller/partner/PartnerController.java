package org.nexuse2e.ui.controller.partner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ParticipantPojo;
import org.nexuse2e.pojo.PartnerPojo;
import org.nexuse2e.ui.form.CollaborationPartnerForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for partner-related stuff.
 *
 * @author Jonas Reese
 */
@Controller
public class PartnerController {

    protected static Logger LOG = Logger.getLogger(PartnerController.class);

    
    @RequestMapping("/ServerIdentities.do")
    public String serverIdentities(
            Model model,
            @RequestParam("type") String type,
            HttpServletRequest request,
            EngineConfiguration engineConfiguration) throws NexusException {

        if (StringUtils.isBlank(type)) {
            type = (String) request.getAttribute( "type" );
        }

        LOG.debug( "type: " + type );

        List<CollaborationPartnerForm> partners = new ArrayList<CollaborationPartnerForm>();
        List<PartnerPojo> partnerPojos = null;
        if ("1".equals(type)) {
            partnerPojos = engineConfiguration.getPartners(
                    Constants.PARTNER_TYPE_LOCAL, Constants.PARTNERCOMPARATOR );

        } else {
            partnerPojos = engineConfiguration.getPartners(
                    Constants.PARTNER_TYPE_PARTNER, Constants.PARTNERCOMPARATOR );
        }

        for (PartnerPojo partner : partnerPojos) {
            CollaborationPartnerForm cpf = new CollaborationPartnerForm();
            cpf.setProperties( partner );
            partners.add( cpf );
        }
        request.setAttribute("TYPE", type);
        if ("1".equals(type)) {
            request.setAttribute("HEADLINE", "Server Identities");
            request.setAttribute("BUTTONTEXT", "Add Server Identity");
        } else {
            request.setAttribute("HEADLINE", "Collaboration Partners");
            request.setAttribute("BUTTONTEXT", "Add Collaboration Partner");
        }

        model.addAttribute("collection", partners);
        
        return "pages/collaboration_partners";
    }
    
    @RequestMapping("/PartnerInfoView.do")
    public String partnerInfoView(
            CollaborationPartnerForm partnerForm, EngineConfiguration engineConfiguration)
                    throws NexusException {

        PartnerPojo partnerPojo = engineConfiguration.getPartnerByNxPartnerId(partnerForm.getNxPartnerId());

        if (partnerPojo != null) {
            partnerForm.setProperties(partnerPojo);

            partnerForm.setChoreographies(new ArrayList<String>());
            List<ChoreographyPojo> choreographies = engineConfiguration.getChoreographies();
            for (ChoreographyPojo choreographyPojo : choreographies) {
                Collection<ParticipantPojo> participants = choreographyPojo.getParticipants();
                for (ParticipantPojo participantPojo : participants) {
                    if (participantPojo.getPartner().getNxPartnerId() == partnerForm.getNxPartnerId()) {
                        partnerForm.addChoreography(choreographyPojo.getName());
                        break;
                    }

                }
            }
        }

        return "pages/partners/partner_info_view";
    }
}
