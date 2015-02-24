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
import java.util.Collection;
import java.util.List;

import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ParticipantPojo;
import org.nexuse2e.pojo.PartnerPojo;
import org.nexuse2e.ui.form.ParticipantForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Choreography participants maintenance controller.
 * 
 * @author Jonas Reese
 */
@Controller
public class ParticipantController {
    @RequestMapping("/ParticipantList.do")
    public String choreographies(
            Model model,
            @RequestParam("nxChoreographyId") int nxChoreographyId,
            EngineConfiguration engineConfiguration) throws NexusException {

        List<ParticipantForm> participants = new ArrayList<ParticipantForm>();
        ChoreographyPojo choreography = engineConfiguration.getChoreographyByNxChoreographyId(nxChoreographyId);
        if (choreography != null) {
            Collection<ParticipantPojo> participantPojos = choreography.getParticipants();
            if (participantPojos != null) {
                for (ParticipantPojo participant : participantPojos) {
                    ParticipantForm pform = new ParticipantForm();
                    pform.setPartnerDisplayName(participant.getPartner().getPartnerId());
                    pform.setNxPartnerId(participant.getPartner().getNxPartnerId());
                    pform.setUrl((participant.getConnection() != null) ? participant.getConnection().getUri() : "");
                    pform.setDescription(participant.getDescription());
                    pform.setNxChoreographyId(nxChoreographyId);
                    participants.add(pform);
                }
            }
        }

        model.addAttribute("collection", participants);
        
        return "pages/choreographies/participants";
    }
    
    @RequestMapping("/ParticipantView.do")
    public String participantView(
            Model model,
            ParticipantForm form,
            EngineConfiguration engineConfiguration) throws NexusException {
        
        ChoreographyPojo choreography = engineConfiguration.getChoreographyByNxChoreographyId(form.getNxChoreographyId());

        List<PartnerPojo> localPartnerList = engineConfiguration.getPartners(Constants.PARTNER_TYPE_LOCAL, Constants.PARTNERCOMPARATOR);
        form.setLocalPartners(localPartnerList);

        ParticipantPojo participant = engineConfiguration.getParticipantFromChoreographyByNxPartnerId(choreography, form.getNxPartnerId());
        form.setProperties(participant);
        form.setPartnerDisplayName(participant.getPartner().getPartnerId());
        form.setNxPartnerId(participant.getPartner().getNxPartnerId());
        form.setUrl(participant.getConnection().getUri());
        form.setConnections(participant.getPartner().getConnections());
        form.setNxConnectionId(participant.getConnection().getNxConnectionId());
        form.setDescription(participant.getDescription());
        form.setNxLocalPartnerId(participant.getLocalPartner().getNxPartnerId());

        form.setLocalCertificates(participant.getLocalPartner().getCertificates());
        if (participant.getLocalCertificate() != null) {
            form.setNxLocalCertificateId(participant.getLocalCertificate().getNxCertificateId());
        } else {
            form.setNxLocalCertificateId(0);
        }

        return "pages/choreographies/participant_view";
    }
}
