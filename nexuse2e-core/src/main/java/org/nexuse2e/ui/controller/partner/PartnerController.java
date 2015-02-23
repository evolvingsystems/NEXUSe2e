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
package org.nexuse2e.ui.controller.partner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.CertificateType;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.GenericComparator;
import org.nexuse2e.configuration.ReferencedCertificateException;
import org.nexuse2e.configuration.ReferencedConnectionException;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ConnectionPojo;
import org.nexuse2e.pojo.ParticipantPojo;
import org.nexuse2e.pojo.PartnerPojo;
import org.nexuse2e.pojo.TRPPojo;
import org.nexuse2e.ui.form.CertificatePromotionForm;
import org.nexuse2e.ui.form.CertificatePropertiesForm;
import org.nexuse2e.ui.form.CollaborationPartnerForm;
import org.nexuse2e.ui.form.PartnerCertificateForm;
import org.nexuse2e.ui.form.PartnerConnectionForm;
import org.nexuse2e.ui.form.ProtectedFileAccessForm;
import org.nexuse2e.util.CertificateUtil;
import org.nexuse2e.util.EncryptionUtil;
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
        
        return "pages/partners/partners";
    }
    
    @RequestMapping("/ServerIdentityDelete.do")
    public String serverIdentityDelete(
            HttpServletRequest request,
            Model model,
            @RequestParam("nxPartnerId") int nxPartnerId,
            EngineConfiguration engineConfiguration) throws NexusException {
        
        PartnerPojo partner = engineConfiguration.getPartnerByNxPartnerId(nxPartnerId);
        if (partner != null && partner.getType() == Constants.PARTNER_TYPE_LOCAL) {
            engineConfiguration.deletePartner(partner);
        }
        
        return serverIdentities(model, Integer.toString(Constants.PARTNER_TYPE_LOCAL), request, engineConfiguration);
    }
    
    @RequestMapping("/CollaborationPartners.do")
    public String collaborationPartners(
            Model model,
            @RequestParam(value = "type", defaultValue = "2") int type,
            EngineConfiguration engineConfiguration) throws NexusException {

        List<CollaborationPartnerForm> partners = new ArrayList<CollaborationPartnerForm>();
        List<PartnerPojo> partnerPojos = null;
        if (type == 1) {
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
        model.addAttribute("TYPE", type);
        if (type == 1) {
            model.addAttribute("HEADLINE", "Server Identities");
            model.addAttribute("BUTTONTEXT", "Add Server Identity");

        } else {
            model.addAttribute("HEADLINE", "Collaboration Partners");
            model.addAttribute("BUTTONTEXT", "Add Collaboration Partner");
        }

        model.addAttribute("collection", partners);

        return "pages/partners/partners";
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
            HttpServletRequest request,
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

        return serverIdentities(model, Integer.toString(partner.getType()), request, engineConfiguration);
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

        PartnerPojo partner = engineConfiguration.getPartnerByNxPartnerId(form.getNxPartnerId());

        // defaults
        form.setReliable(true);
        form.setRetries(3);
        form.setTimeout(30);
        form.setMessageInterval(30);

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

        PartnerPojo partner = engineConfiguration.getPartnerByNxPartnerId(form.getNxPartnerId());
        try {
            ConnectionPojo connection = engineConfiguration.getConnectionFromPartnerByNxConnectionId(partner, form.getNxConnectionId());
            if (connection != null) {
                engineConfiguration.deleteConnection( connection );
            }
        } catch (ReferencedConnectionException e) {
            for (ParticipantPojo participant : e.getReferringObjects()) {
                bindingResult.reject(
                        "error.referenced.object.connection",
                        new Object[] { participant.getChoreography().getName() },
                        "Connection referenced by choreography " + participant.getChoreography().getName());
            }
            return partnerConnectionView(form, engineConfiguration);
        }

        CollaborationPartnerForm collaborationPartnerForm = new CollaborationPartnerForm();
        collaborationPartnerForm.setNxPartnerId(partner.getNxId());
        model.addAttribute("collaborationPartnerForm", collaborationPartnerForm);
        return partnerConnectionList(collaborationPartnerForm, engineConfiguration);
    }

    @RequestMapping("/PartnerCertificateList.do")
    public String partnerCertificateList(
            CollaborationPartnerForm form,
            EngineConfiguration engineConfiguration)
                    throws NexusException,
                    KeyStoreException,
                    NoSuchProviderException,
                    NoSuchAlgorithmException,
                    CertificateException,
                    IOException {

        PartnerPojo partnerPojo = engineConfiguration.getPartnerByNxPartnerId(form.getNxPartnerId());
        form.setProperties(partnerPojo);

        Set<CertificatePojo> certs = partnerPojo.getCertificates();
        for (CertificatePojo cert : certs) {
            CertificatePropertiesForm formCert = new CertificatePropertiesForm();
            formCert.setCertificateProperties( cert );
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

    @RequestMapping("/PartnerCertificateAdd.do")
    public String partnerCertificateAdd(
            @RequestParam("partnerId") String partnerId,
            ProtectedFileAccessForm form,
            EngineConfiguration engineConfiguration) throws NexusException {

            form.setId( partnerId );
            return "pages/partners/partner_certificate_add";
        }

    @RequestMapping("/PartnerCertificateView.do")
    public String partnerCertificateView(Model model, PartnerCertificateForm form, EngineConfiguration engineConfiguration)
            throws NexusException, KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException, CertificateException, IOException {

        int nxCertificateId = form.getNxCertificateId();
        int nxPartnerId = form.getNxPartnerId();

        PartnerPojo partner = engineConfiguration.getPartnerByNxPartnerId(nxPartnerId);
        CertificatePojo cert = engineConfiguration.getCertificateFromPartnerByNxCertificateId(partner, nxCertificateId);

        if (cert != null) {
            byte[] data = cert.getBinaryData();
            X509Certificate x509Certificate = null;
            List<X509Certificate> allCertificates = new ArrayList<X509Certificate>();
    
            if (cert.getType() == CertificateType.PARTNER.getOrdinal()) {
                x509Certificate = CertificateUtil.getX509Certificate(data);
            } else if (cert.getType() == CertificateType.LOCAL.getOrdinal()) {
    
                KeyStore jks = KeyStore.getInstance(CertificateUtil.DEFAULT_KEY_STORE, CertificateUtil.DEFAULT_JCE_PROVIDER);
                jks.load(new ByteArrayInputStream(cert.getBinaryData()), EncryptionUtil.decryptString(cert.getPassword()).toCharArray());
                if (jks != null) {

                    Enumeration<String> aliases = jks.aliases();
                    if (!aliases.hasMoreElements()) {
                        throw new NexusException("no certificate aliases found");
                    }
                    while (aliases.hasMoreElements()) {
                        String tempAlias = aliases.nextElement();
                        if (jks.isKeyEntry(tempAlias)) {
                            java.security.cert.Certificate[] certArray = jks.getCertificateChain(tempAlias);
                            if (certArray != null) {
                                x509Certificate = (X509Certificate) certArray[0];
                                for (int i = 0; i < certArray.length; i++) {
                                    allCertificates.add((X509Certificate) certArray[i]);
                                }
                            }
                        }
                    }
                }
            }
    
            List<CertificatePropertiesForm> certificateList = new ArrayList<CertificatePropertiesForm>();
            if (x509Certificate != null) {
                form.setCertificateProperties(x509Certificate);
            }
    
            form.setNxCertificateId(cert.getNxCertificateId());
            form.setNxPartnerId(nxPartnerId);
            form.setCertificateId(cert.getName());
            CertificatePromotionForm certs = new CertificatePromotionForm();
    
            for (CertificatePojo certificate : engineConfiguration.getCertificates(CertificateType.CA.getOrdinal(), null)) {
                byte[] b = certificate.getBinaryData();
                if (b != null && b.length > 0) {
                    try {
                        X509Certificate cacert = CertificateUtil.getX509Certificate(b);
                        if (cacert != null) { // #39
                            allCertificates.add(cacert);
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
    
            Certificate[] chain = CertificateUtil.getCertificateChain(x509Certificate, allCertificates);
            for (int i = 0; i < chain.length; i++) {
                CertificatePropertiesForm f = new CertificatePropertiesForm();
                X509Certificate certificate = (X509Certificate) chain[i];
                f.setCertificateProperties(certificate);
                certificateList.add(f);
    
                if (i + 1 == chain.length && !certificate.getSubjectX500Principal().getName().equals(certificate.getIssuerX500Principal().getName())) {
                    f = new CertificatePropertiesForm();
                    f.setPrincipal(CertificateUtil.getPrincipalFromCertificate(certificate, false));
                    f.setNxCertificateId(-1); // indicate "missing"
                    f.setFingerprint(CertificateUtil.getPrincipalFromCertificate(certificate, false).getName());
                    certificateList.add(f);
                }
            }
    
            certs.setCertificateParts(certificateList);
            model.addAttribute("certs", certificateList);
        }

        return "pages/partners/partner_certificate_view";
    }
    
    @RequestMapping("/PartnerCertificateDelete.do")
    public String partnerCertificateDelete(
            Model model,
            CollaborationPartnerForm partnerForm,
            PartnerCertificateForm form,
            BindingResult bindingResult,
            EngineConfiguration engineConfiguration)
                    throws KeyStoreException,
                    NoSuchProviderException,
                    NoSuchAlgorithmException,
                    CertificateException,
                    NexusException,
                    IOException {

        int nxPartnerId = form.getNxPartnerId();
        int nxCertificateId = form.getNxCertificateId();

        try {
            PartnerPojo partner = engineConfiguration.getPartnerByNxPartnerId(nxPartnerId);
            CertificatePojo certificate = engineConfiguration.getCertificateFromPartnerByNxCertificateId(
                    partner, nxCertificateId);
            if (certificate != null) {
                engineConfiguration.deleteCertificate(certificate);
            }
        } catch (ReferencedCertificateException e) {
            for (ConnectionPojo connection : e.getReferringObjects()) {
                bindingResult.reject(
                        "error.referenced.object.certificate",
                        new Object[]{ connection.getName() },
                        "Certificate still used by connection " + connection.getName());
            }
            return partnerCertificateView(model, form, engineConfiguration);
        }
        return partnerCertificateList(partnerForm, engineConfiguration);
    }
    
    @RequestMapping("/PartnerCertificateSave.do")
    public String partnerCertificateSave(
            Model model, CollaborationPartnerForm collaborationPartnerForm, PartnerCertificateForm form, EngineConfiguration engineConfiguration)
                    throws NexusException, KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException, CertificateException, IOException {

        int nxPartnerId = form.getNxPartnerId();
        int nxCertificateId = form.getNxCertificateId();

        LOG.debug("nxCertficateId: " + nxCertificateId);
        LOG.debug("nxPartnerId: " + nxPartnerId);
        PartnerPojo partner = engineConfiguration.getPartnerByNxPartnerId(nxPartnerId);
        CertificatePojo cPojo = engineConfiguration.getCertificateFromPartnerByNxCertificateId(partner, nxCertificateId);

        if (cPojo != null) {
            cPojo.setName(form.getCertificateId());
            engineConfiguration.updateCertificate(cPojo);
    
            if (cPojo.getType() == CertificateType.LOCAL.getOrdinal()) {
                model.addAttribute("type", "1");
            } else {
                model.addAttribute("type", "2");
            }
        }

        return partnerCertificateList(collaborationPartnerForm, engineConfiguration);
    }
}
