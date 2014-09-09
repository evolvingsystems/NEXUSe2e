package org.nexuse2e.ui.controller.partner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.GenericComparator;
import org.nexuse2e.configuration.ReferencedConnectionException;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ConnectionPojo;
import org.nexuse2e.pojo.ParticipantPojo;
import org.nexuse2e.pojo.PartnerPojo;
import org.nexuse2e.pojo.TRPPojo;
import org.nexuse2e.ui.form.CollaborationPartnerForm;
import org.nexuse2e.ui.form.CollaborationPartnerForm.Certificate;
import org.nexuse2e.ui.form.PartnerConnectionForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

        model.addAttribute("collection", partners);
        
        return "pages/collaboration_partners";
    }
    
    @RequestMapping("/CollaborationPartnerAdd.do")
    public String collaborationPartnerAdd(@RequestParam("type") int type, CollaborationPartnerForm form)
                    throws NexusException {

        form.cleanSettings();

        if (type == 1) {
            form.setType(1);
        } else {
            form.setType(2);
        }

        return "pages/partners/partner_add";
    }

    @RequestMapping("/CollaborationPartnerCreate.do")
    public String collaborationPartnerCreate(
            @Valid CollaborationPartnerForm form,
            BindingResult bindingResult,
            Model model,
            EngineConfiguration engineConfiguration) throws NexusException {

        PartnerPojo partner = new PartnerPojo();
        form.getProperties(partner);
        partner.setNxPartnerId(0);

        if (engineConfiguration.getPartnerByPartnerId(partner.getPartnerId()) != null) {
            bindingResult.rejectValue("partnerId", "partner.error.partnerid.unique", "Partner ID must be unique");
        }

        if (bindingResult.hasErrors()) {
            return "pages/partners/partner_add";
        }
        
        engineConfiguration.getPartners(0, null).add(partner);
        engineConfiguration.updatePartner(partner);

        model.addAttribute("refreshTree", "true");

        return "";
    }
    
    @RequestMapping("/PartnerInfoView.do")
    public String partnerInfoView(
            CollaborationPartnerForm partnerForm, EngineConfiguration engineConfiguration)
                    throws NexusException {

        PartnerPojo partnerPojo = engineConfiguration.getPartnerByNxPartnerId(partnerForm.getNxPartnerId());
        partnerForm.setType(partnerPojo.getType());

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

    @RequestMapping("/PartnerConnectionList.do")
    public String partnerConnectionList(
            CollaborationPartnerForm form,
            EngineConfiguration engineConfiguration) throws NexusException {

        int id = form.getNxPartnerId();

        PartnerPojo partner = engineConfiguration.getPartnerByNxPartnerId(id);
        form.setProperties(partner);
        form.setConnections(new TreeSet<PartnerConnectionForm>(new GenericComparator<PartnerConnectionForm>("name", true)));
        for (ConnectionPojo con : partner.getConnections()) {
            PartnerConnectionForm formCon = new PartnerConnectionForm();
            formCon.setProperties(con);
            form.addConnection(formCon);
        }

        return "pages/partners/partner_connection_list";
    }
    
    @RequestMapping("/PartnerConnectionAdd.do")
    public String partnerConnectionAdd(
            PartnerConnectionForm form,
            EngineConfiguration engineConfiguration) throws NexusException {

        form.cleanSettings();

        // defaults
        form.setReliable(true);
        form.setRetries(3);
        form.setTimeout(30);
        form.setMessageInterval(30);

        PartnerPojo partner = engineConfiguration.getPartnerByPartnerId(form.getPartnerId());
        form.setCertificates(partner.getCertificates());
        form.setTrps(engineConfiguration.getTrps());

        return "pages/partners/partner_connection_add";
    }

    @RequestMapping("/PartnerConnectionCreate.do")
    public String partnerConnectionCreate(
            @Valid PartnerConnectionForm form,
            BindingResult bindingResult,
            Model model,
            EngineConfiguration engineConfiguration) throws NexusException {

        PartnerPojo partner = engineConfiguration.getPartnerByPartnerId(form.getPartnerId());

        if (bindingResult.hasErrors()) {
            form.setCertificates(partner.getCertificates());
            form.setTrps(engineConfiguration.getTrps());

            return "pages/partners/partner_connection_add";
        }

        ConnectionPojo connection = new ConnectionPojo();
        form.getProperties(connection);
        int nxCertificateId = form.getNxCertificateId();
        CertificatePojo certificate = engineConfiguration.getCertificateFromPartnerByNxCertificateId(partner, nxCertificateId);
        connection.setCertificate( certificate );
        TRPPojo trp = engineConfiguration.getTrpByNxTrpId(form.getNxTrpId());
        connection.setTrp(trp);
        connection.setPartner(partner);
        connection.setCreatedDate(new Date());
        connection.setModifiedDate(new Date());
        partner.getConnections().add(connection);
        engineConfiguration.updatePartner(partner);
        
        CollaborationPartnerForm collaborationPartnerForm = new CollaborationPartnerForm();
        collaborationPartnerForm.setNxPartnerId(partner.getNxId());
        model.addAttribute("collaborationPartnerForm", collaborationPartnerForm);
        return partnerConnectionList(collaborationPartnerForm, engineConfiguration);
    }
    
    @RequestMapping("/PartnerConnectionSave.do")
    public String partnerConnectionSave(
            @Valid PartnerConnectionForm form,
            BindingResult bindingResult,
            Model model,
            EngineConfiguration engineConfiguration) throws NexusException {

        PartnerPojo partner = engineConfiguration.getPartnerByNxPartnerId(form.getNxPartnerId());

        if (bindingResult.hasErrors()) {
            form.setCertificates(partner.getCertificates());
            form.setTrps(engineConfiguration.getTrps());

            return "pages/partners/partner_connection_view";
        }

        ConnectionPojo connection = engineConfiguration.getConnectionFromPartnerByNxConnectionId(partner, form.getNxConnectionId());
        form.getProperties(connection);
        engineConfiguration.updatePartner(partner);
        
        CollaborationPartnerForm collaborationPartnerForm = new CollaborationPartnerForm();
        collaborationPartnerForm.setNxPartnerId(partner.getNxId());
        model.addAttribute("collaborationPartnerForm", collaborationPartnerForm);
        return partnerConnectionList(collaborationPartnerForm, engineConfiguration);
    }

    @RequestMapping("/PartnerConnectionView.do")
    public String partnerConnectionView(
            PartnerConnectionForm form,
            EngineConfiguration engineConfiguration) throws NexusException {

        PartnerPojo partner = engineConfiguration.getPartnerByNxPartnerId(form.getNxPartnerId());
        ConnectionPojo connection = engineConfiguration.getConnectionFromPartnerByNxConnectionId(partner, form.getNxConnectionId());
        form.setProperties(connection);
        form.setCertificates(partner.getCertificates());
        if (connection.getCertificate() != null) {
            form.setNxCertificateId( connection.getCertificate().getNxCertificateId() );
        }

        form.setTrps(engineConfiguration.getTrps());
        form.setNxTrpId(connection.getTrp().getNxTRPId());

        return "pages/partners/partner_connection_view";
    }

    @RequestMapping("/PartnerConnectionDelete.do")
    public String partnerConnectionDelete(
            PartnerConnectionForm form,
            BindingResult bindingResult,
            Model model,
            EngineConfiguration engineConfiguration) throws NexusException {

        PartnerPojo partner = engineConfiguration.getPartnerByPartnerId(form.getPartnerId());
        try {
            ConnectionPojo connection = engineConfiguration.getConnectionFromPartnerByNxConnectionId(partner, form.getNxConnectionId());
            engineConfiguration.deleteConnection( connection );
        } catch (ReferencedConnectionException e) {
            for (ParticipantPojo participant : e.getReferringObjects()) {
                bindingResult.reject(
                        "error.referenced.object.connection",
                        new Object[] { participant.getChoreography().getName() },
                        "Connection referenced by choreography " + participant.getChoreography().getName());
            }
            return "pages/partners/partner_connection_view";
        }

        CollaborationPartnerForm collaborationPartnerForm = new CollaborationPartnerForm();
        collaborationPartnerForm.setNxPartnerId(partner.getNxId());
        model.addAttribute("collaborationPartnerForm", collaborationPartnerForm);
        return partnerConnectionList(collaborationPartnerForm, engineConfiguration);
    }

    @RequestMapping("/PartnerCertificateList.do")
    public String partnerCertificateList(
            CollaborationPartnerForm form,
            EngineConfiguration engineConfiguration) throws NexusException {

        PartnerPojo partnerPojo = engineConfiguration.getPartnerByNxPartnerId(form.getNxPartnerId());
        form.setProperties(partnerPojo);

        Set<CertificatePojo> certs = partnerPojo.getCertificates();
        form.setCertificates( new ArrayList<Certificate>() );
        for (CertificatePojo cert : certs) {
            CollaborationPartnerForm.Certificate formCert = form.new Certificate();
            formCert.setProperties( cert );
            form.addCertificate( formCert );
        }
        
        return "pages/partners/partner_certificate_list";
    }
    
    @RequestMapping("/UpdatePartnerInfo.do")
    public String updatePartnerInfo(
            @Valid CollaborationPartnerForm form,
            EngineConfiguration engineConfiguration) throws NexusException {

        int nxPartnerId = form.getNxPartnerId();

        PartnerPojo partner = engineConfiguration.getPartnerByNxPartnerId(nxPartnerId);
        if (partner == null) {
            LOG.trace("Error, partner not found for id: " + nxPartnerId);
        } else {
            int partnerType = partner.getType();
            form.getProperties(partner);
            partner.setType(partnerType);
            engineConfiguration.updatePartner(partner);
        }
        
        return partnerInfoView(form, engineConfiguration);
    }
}
