/**
 * NEXUSe2e Business Messaging Open Source  
 * Copyright 2007, Tamgroup and X-ioma GmbH   
 *  
 * This is free software; you can redistribute it and/or modify it  
 * under the terms of the GNU Lesser General Public License as  
 * published by the Free Software Foundation version 2.1 of  
 * the License.  
 *  
 * This software is distributed in the hope that it will be useful,  
 * but WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU  
 * Lesser General Public License for more details.  
 *  
 * You should have received a copy of the GNU Lesser General Public  
 * License along with this software; if not, write to the Free  
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.nexuse2e.configuration;

import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.backend.BackendPipelineDispatcher;
import org.nexuse2e.configuration.Constants.ComponentType;
import org.nexuse2e.pojo.ActionPojo;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ComponentPojo;
import org.nexuse2e.pojo.ConnectionPojo;
import org.nexuse2e.pojo.LoggerPojo;
import org.nexuse2e.pojo.ParticipantPojo;
import org.nexuse2e.pojo.PartnerPojo;
import org.nexuse2e.pojo.PipelinePojo;
import org.nexuse2e.pojo.RolePojo;
import org.nexuse2e.pojo.ServicePojo;
import org.nexuse2e.pojo.TRPPojo;
import org.nexuse2e.pojo.UserPojo;
import org.nexuse2e.service.Service;
import org.nexuse2e.ui.structure.impl.CachedXmlStructureServer;
import org.nexuse2e.util.CertificateUtil;
import org.nexuse2e.util.EncryptionUtil;

/**
 * @author gesch
 *
 */
public class ConfigurationAccessService {

    Logger                      LOG                = Logger.getLogger( ConfigurationAccessService.class );
    private EngineConfiguration engineConfig;
    private boolean             validConfiguration = false;

    /**
     * @param engineConfig
     */
    public ConfigurationAccessService( EngineConfiguration engineConfig ) {

        if ( engineConfig != null ) {
            this.engineConfig = engineConfig;
            validConfiguration = true;
        }
    }
    
    /**
     * @param nxPipelineId
     * @return
     */
    public PipelinePojo getPipelinePojoByNxPipelineId( int nxPipelineId ) {

        PipelinePojo pipeline = null;

        List<PipelinePojo> pipelines = getBackendPipelinePojos( Constants.PIPELINE_TYPE_ALL, null );
        Iterator<PipelinePojo> pipelineI = pipelines.iterator();
        while ( pipelineI.hasNext() ) {
            pipeline = pipelineI.next();
            if ( pipeline.getNxPipelineId() == nxPipelineId ) {
                return pipeline;
            }
        }

        pipelines = getFrontendPipelinePojos( Constants.PIPELINE_TYPE_ALL, null );
        pipelineI = pipelines.iterator();
        while ( pipelineI.hasNext() ) {
            pipeline = pipelineI.next();
            if ( pipeline.getNxPipelineId() == nxPipelineId ) {
                return pipeline;
            }
        }
        return null;
    }

    /**
     * @param name
     * @return
     */
    public PipelinePojo getPipelineByName( String name ) {

        PipelinePojo pipeline = null;

        List<PipelinePojo> pipelines = getBackendPipelinePojos( Constants.PIPELINE_TYPE_ALL, null );
        Iterator<PipelinePojo> pipelineI = pipelines.iterator();
        while ( pipelineI.hasNext() ) {
            pipeline = pipelineI.next();
            if ( pipeline.getName().equals( name ) ) {
                return pipeline;
            }
        }
        pipelines = getFrontendPipelinePojos( Constants.PIPELINE_TYPE_ALL, null );
        pipelineI = pipelines.iterator();
        while ( pipelineI.hasNext() ) {
            pipeline = pipelineI.next();
            if ( pipeline.getName().equals( name ) ) {
                return pipeline;
            }
        }
        return null;
    }

    /**
     * @param nxServiceId
     * @return
     */
    public ServicePojo getServicePojoByNxServiceId( int nxServiceId ) {

        for ( ServicePojo service : engineConfig.getServices() ) {
            if ( service.getNxServiceId() == nxServiceId ) {
                return service;
            }
        }
        return null;
    }
    
    /**
     * @param name
     * @return
     */
    public ServicePojo getServicePojoName( String name ) {
        for ( ServicePojo service : engineConfig.getServices() ) {
            if ( service.getName().equals( name ) ) {
                return service;
            }
        }
        return null;
    }
    
    
    /**
     * Return the BackendPipelineDispatcher instance
     * @return The BackendPipelineDispatcher instance
     */
    public BackendPipelineDispatcher getBackendPipelineDispatcher() {
        if(engineConfig != null) {
            return engineConfig.getBackendPipelineDispatcher();
        }
        return null;
    } // getBackendPipelineDispatcher

    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<PipelinePojo> getFrontendPipelinePojos( int type, GenericComparator comparator ) {

        if(engineConfig == null) {
            return null;
        }
        
        List<PipelinePojo> filteredList = null;
        if ( type != Constants.PIPELINE_TYPE_ALL ) {
            filteredList = new ArrayList<PipelinePojo>();
            Iterator<PipelinePojo> i = engineConfig.getFrontendPipelineTemplates().iterator();
            while ( i.hasNext() ) {
                PipelinePojo pipeline = i.next();
                if ( type == Constants.PIPELINE_TYPE_INBOUND && !pipeline.isOutbound() ) {
                    filteredList.add( pipeline );
                } else if ( type == Constants.PIPELINE_TYPE_OUTBOUND && pipeline.isOutbound() ) {
                    filteredList.add( pipeline );
                }
            }
        } else {
            filteredList = engineConfig.getFrontendPipelineTemplates();
        }
        if ( comparator != null ) {
            Collections.sort( filteredList, comparator );
        }
        return filteredList;
    }

    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<PipelinePojo> getBackendPipelinePojos( int type, GenericComparator comparator ) {

        if(engineConfig == null) {
            return null;
        }
        List<PipelinePojo> filteredList = null;
        if ( type != Constants.PIPELINE_TYPE_ALL ) {
            filteredList = new ArrayList<PipelinePojo>();
            Iterator<PipelinePojo> i = engineConfig.getBackendPipelineTemplates().iterator();
            while ( i.hasNext() ) {
                PipelinePojo pipeline = i.next();
                if ( type == Constants.PIPELINE_TYPE_INBOUND && !pipeline.isOutbound() ) {
                    filteredList.add( pipeline );
                } else if ( type == Constants.PIPELINE_TYPE_OUTBOUND && pipeline.isOutbound() ) {
                    filteredList.add( pipeline );
                }
            }
        } else {
            filteredList = engineConfig.getBackendPipelineTemplates();
        }
        if ( comparator != null ) {
            Collections.sort( filteredList, comparator );
        }
        return filteredList;
    }

    /**
     * @return
     */
    public List<TRPPojo> getTrps() {
        if(engineConfig != null) {
            return engineConfig.getTrps();
        }
        return null;
    }

    /**
     * @param nxTrpId
     * @return
     */
    public TRPPojo getTrpByNxTrpId( int nxTrpId ) {

        TRPPojo trp = null;

        List<TRPPojo> trps = getTrps();
        Iterator<TRPPojo> trpI = trps.iterator();
        while ( trpI.hasNext() ) {
            trp = trpI.next();
            if ( trp.getNxTRPId() == nxTrpId ) {
                return trp;
            }
        }
        return null;
    }

    /**
     * @param protocol
     * @param version
     * @param transport
     * @return
     */
    public TRPPojo getTrpByProtocolVersionAndTransport( String protocol, String version, String transport ) {

        List<TRPPojo> trps = getTrps();
        for ( TRPPojo trp : trps ) {
            if ( StringUtils.equals( trp.getProtocol(), protocol ) && StringUtils.equals( trp.getVersion(), version )
                    && StringUtils.equals( trp.getTransport(), transport ) ) {
                return trp;
            }
        }

        return null;
    }

    /**
     * @param nxPartnerId
     * @return
     * @throws NexusException
     */
    public PartnerPojo getPartnerByNxPartnerId( int nxPartnerId ) throws NexusException {

        if ( !validConfiguration ) {
            throw new NexusException( "Configuration Invalid!" );
        }
        List<PartnerPojo> partners = engineConfig.getPartners();
        if ( partners != null && partners.size() > 0 ) {
            Iterator<PartnerPojo> i = partners.iterator();
            while ( i.hasNext() ) {
                PartnerPojo partner = i.next();
                if ( partner.getNxPartnerId() == nxPartnerId ) {
                    return partner;
                }
            }
        }
        return null;
    }

    /**
     * @param nxComponentId
     * @return
     * @throws NexusException
     */
    public ComponentPojo getComponentByNxComponentId( int nxComponentId ) throws NexusException {

        if ( !validConfiguration ) {
            throw new NexusException( "Configuration Invalid!" );
        }
        List<ComponentPojo> components = engineConfig.getComponents();
        if ( components != null && components.size() > 0 ) {
            Iterator<ComponentPojo> i = components.iterator();
            while ( i.hasNext() ) {
                ComponentPojo component = i.next();
                if ( component.getNxComponentId() == nxComponentId ) {
                    return component;
                }
            }
        }
        return null;
    }

    /**
     * @param partnerId
     * @return
     * @throws NexusException
     */
    public PartnerPojo getPartnerByPartnerId( String partnerId ) throws NexusException {

        if ( !validConfiguration ) {
            throw new NexusException( "Configuration Invalid!" );
        }
        List<PartnerPojo> partners = engineConfig.getPartners();
        if ( partners != null && partners.size() > 0 ) {
            Iterator<PartnerPojo> i = partners.iterator();
            while ( i.hasNext() ) {
                PartnerPojo partner = i.next();
                if ( partner.getPartnerId().equals( partnerId ) ) {
                    return partner;
                }
            }
        }
        return null;
    }

    /**
     * @param partner
     * @param nxCertificateId
     * @return
     */
    public CertificatePojo getCertificateFromPartnerByNxCertificateId( PartnerPojo partner, int nxCertificateId ) {

        if ( partner == null ) {
            return null;
        }
        CertificatePojo certificate = null;
        Set<CertificatePojo> certificates = partner.getCertificates();
        if ( certificates == null || certificates.size() == 0 ) {
            return null;
        }
        Iterator<CertificatePojo> certificateI = certificates.iterator();
        while ( certificateI.hasNext() ) {
            certificate = certificateI.next();
            if ( certificate.getNxCertificateId() == nxCertificateId ) {
                return certificate;
            }
        }

        return null;
    }

    /**
     * @param partner
     * @param nxConnectionId
     * @return
     */
    public ConnectionPojo getConnectionFromPartnerByNxConnectionId( PartnerPojo partner, int nxConnectionId ) {

        if ( partner == null ) {
            return null;
        }
        ConnectionPojo connection = null;
        Set<ConnectionPojo> connections = partner.getConnections();
        if ( connections == null || connections.size() == 0 ) {
            return null;
        }
        Iterator<ConnectionPojo> connectionI = connections.iterator();
        while ( connectionI.hasNext() ) {
            connection = connectionI.next();
            if ( connection.getNxConnectionId() == nxConnectionId ) {
                return connection;
            }
        }

        return null;
    }

    /**
     * @return
     * @throws NexusException
     */
    public List<ChoreographyPojo> getChoreographies() throws NexusException {
        
        if(engineConfig != null) {
            return engineConfig.getChoreographies();
        }
        return null;
    }

    /**
     * @param type
     * @param comparator
     * @return
     * @throws NexusException
     */
    @SuppressWarnings("unchecked")
    public List<ComponentPojo> getComponents( ComponentType type, Comparator comparator ) throws NexusException {

        if(engineConfig == null) {
            return null;
        }
        List<ComponentPojo> filteredList = null;
        if ( type != ComponentType.ALL ) {
            filteredList = new ArrayList<ComponentPojo>();
            for ( ComponentPojo component : engineConfig.getComponents() ) {
                if ( type.getValue() == component.getType() ) {
                    filteredList.add( component );
                }
            }
        } else {
            filteredList = engineConfig.getComponents();
        }
        if ( comparator != null ) {
            Collections.sort( filteredList, comparator );
        }
        return filteredList;
    }

    /**
     * @param type 0=all, 1=local, 2=partner
     * @param comparator
     * @return
     * @throws NexusException
     */
    @SuppressWarnings("unchecked")
    public List<PartnerPojo> getPartners( int type, Comparator comparator ) throws NexusException {

        if(engineConfig == null) {
            return null;
        }
        List<PartnerPojo> filteredList = null;
        if ( type != Constants.PARTNER_TYPE_ALL ) {
            filteredList = new ArrayList<PartnerPojo>();
            Iterator<PartnerPojo> i = engineConfig.getPartners().iterator();
            while ( i.hasNext() ) {
                PartnerPojo partner = i.next();

                if ( type == partner.getType() ) {
                    filteredList.add( partner );
                }
            }
        } else {
            filteredList = engineConfig.getPartners();
        }
        if ( comparator != null ) {
            Collections.sort( filteredList, comparator );
        }
        return filteredList;
    }

    /**
     * @param type 0=all, 1=local, 2=partner
     * @param comparator
     * @return
     * @throws NexusException
     */
    @SuppressWarnings("unchecked")
    public List<CertificatePojo> getCertificates( int type, Comparator comparator ) throws NexusException {

        if(engineConfig == null) {
            return null;
        }
        List<CertificatePojo> filteredList = null;
        if ( type != 0 ) {
            filteredList = new ArrayList<CertificatePojo>();
            for ( CertificatePojo certificate : engineConfig.getCertificates() ) {

                if ( type == Constants.CERTIFICATE_TYPE_ALL || type == certificate.getType() ) {
                    filteredList.add( certificate );
                }
            }
        } else {
            filteredList = engineConfig.getCertificates();
        }
        if ( comparator != null ) {
            Collections.sort( filteredList, comparator );
        }
        return filteredList;
    }

    /**
     * @param type
     * @param name
     * @return
     * @throws NexusException
     */
    public CertificatePojo getCertificateByName( int type, String name ) throws NexusException {

        List<CertificatePojo> certificates = getCertificates( type, null );
        for ( CertificatePojo certificate : certificates ) {
            if ( certificate.getName().equals( name ) ) {
                return certificate;
            }
        }
        return null;
    }

    /**
     * @param type
     * @param nxCertificateId
     * @return
     * @throws NexusException
     */
    public CertificatePojo getCertificateByNxCertificateId( int type, int nxCertificateId ) throws NexusException {

        List<CertificatePojo> certificates = getCertificates( type, null );
        for ( CertificatePojo certificate : certificates ) {
            if ( certificate.getNxCertificateId() == nxCertificateId ) {
                return certificate;
            }
        }
        return null;
    }

    /**
     * @param type certificate type, defined in configuration.constants. 
     * @param isUnique if true, method throws exception if there is more than one element of selected type in database
     * @return
     * @throws NexusException
     */
    public CertificatePojo getFirstCertificateByType( int type, boolean isUnique ) throws NexusException {

        List<CertificatePojo> certificates = getCertificates( type, null );
        if ( certificates == null || certificates.size() == 0 ) {
            return null;
        }
        if ( isUnique && certificates.size() > 1 ) {
            throw new NexusException( "There is more than one certificate of type: " + type + " in database" );
        }
        return certificates.get( 0 );
    }

    /**
     * @param choreographyId
     * @return
     * @throws NexusException
     */
    public ChoreographyPojo getChoreographyByChoreographyId( String choreographyId ) throws NexusException {

        if ( !validConfiguration ) {
            throw new NexusException( "Configuration Invalid!" );
        }
        List<ChoreographyPojo> choreographies = engineConfig.getChoreographies();
        if ( choreographies != null && choreographies.size() > 0 ) {
            Iterator<ChoreographyPojo> i = choreographies.iterator();
            while ( i.hasNext() ) {
                ChoreographyPojo choreography = i.next();
                if ( choreography.getName().equals( choreographyId ) ) {
                    return choreography;
                }
            }
        }
        return null;
    }

    /**
     * @param nxChoreographyId
     * @return
     * @throws NexusException
     */
    public ChoreographyPojo getChoreographyByNxChoreographyId( int nxChoreographyId ) throws NexusException {

        if ( !validConfiguration ) {
            throw new NexusException( "Configuration Invalid!" );
        }
        List<ChoreographyPojo> choreographies = engineConfig.getChoreographies();
        if ( choreographies != null && choreographies.size() > 0 ) {
            Iterator<ChoreographyPojo> i = choreographies.iterator();
            while ( i.hasNext() ) {
                ChoreographyPojo choreography = i.next();
                if ( choreography.getNxChoreographyId() == nxChoreographyId ) {
                    return choreography;
                }
            }
        }
        return null;
    }

    /**
     * @param choreography
     * @param participantId
     * @return
     */
    public ParticipantPojo getParticipantFromChoreographyByNxPartnerId( ChoreographyPojo choreography, int nxPartnerId ) {

        List<ParticipantPojo> participants = choreography.getParticipants();
        ParticipantPojo participant = null;
        Iterator<ParticipantPojo> i = participants.iterator();
        while ( i.hasNext() ) {
            participant = i.next();
            if ( participant.getPartner().getNxPartnerId() == nxPartnerId ) {
                return participant;
            }
        }
        return null;
    }

    /**
     * @param choreography
     * @param nxActionId
     * @return
     */
    public ActionPojo getActionFromChoreographyByNxActionId( ChoreographyPojo choreography, int nxActionId ) {

        Set<ActionPojo> actions = choreography.getActions();
        if ( actions != null && actions.size() > 0 ) {
            Iterator<ActionPojo> i = actions.iterator();
            while ( i.hasNext() ) {
                ActionPojo action = i.next();
                if ( action.getNxActionId() == nxActionId ) {
                    return action;
                }
            }
        }
        return null;
    }

    /**
     * @param choreography
     * @param actionId
     * @return
     */
    public ActionPojo getActionFromChoreographyByActionId( ChoreographyPojo choreography, String actionId ) {

        Set<ActionPojo> actions = choreography.getActions();
        if ( actions != null && actions.size() > 0 ) {
            Iterator<ActionPojo> i = actions.iterator();
            while ( i.hasNext() ) {
                ActionPojo action = i.next();
                if ( action.getName().equals( actionId ) ) {
                    return action;
                }
            }
        }
        return null;
    }

    /**
     * @param choreography
     * @param partner
     * @return
     */
    public ParticipantPojo getParticipantFromChoreographyByPartner( ChoreographyPojo choreography, PartnerPojo partner ) {

        List<ParticipantPojo> participants = choreography.getParticipants();
        if ( participants != null && participants.size() > 0 ) {
            Iterator<ParticipantPojo> i = participants.iterator();
            while ( i.hasNext() ) {
                ParticipantPojo participant = i.next();

                if ( participant.getPartner().getNxPartnerId() == partner.getNxPartnerId() ) {
                    return participant;
                }
            }
        }
        return null;
    }

    /**
     * @return the engineConfig
     */
    public EngineConfiguration getEngineConfig() {

        return engineConfig;
    }
    
    /**
     * @return the validConfiguration
     */
    public boolean isValidConfiguration() {

        return validConfiguration;
    }

    /**
     * @param choreography
     */
    public void updatePartner( PartnerPojo partner ) {

        try {
            PartnerPojo oldPartner = getPartnerByNxPartnerId( partner.getNxPartnerId() );
            if ( oldPartner != null ) {
                getPartners( 0, null ).remove( oldPartner );
            }
            getPartners( 0, null ).add( partner );

            //engineConfig.saveConfigurationToDB();
            applyConfiguration();
        } catch ( NexusException e ) {
            e.printStackTrace();
        }
    }

    /**
     * @param choreography
     */
    public void updateChoreography( ChoreographyPojo choreography ) {

        try {
            ChoreographyPojo oldChoreography = getChoreographyByChoreographyId( choreography.getName() );
            if ( oldChoreography != null ) {
                getChoreographies().remove( oldChoreography );
            }
            getChoreographies().add( choreography );

            //engineConfig.saveConfigurationToDB();
            applyConfiguration();
        } catch ( NexusException e ) {
            e.printStackTrace();
        }
    }

    /**
     * @param choreography
     */
    public void deleteChoreography( ChoreographyPojo choreography ) {

        try {
            ChoreographyPojo oldChoreography = getChoreographyByChoreographyId( choreography.getName() );
            if ( oldChoreography != null ) {
                getChoreographies().remove( oldChoreography );
            }
            engineConfig.deleteChoreographyInDB( choreography );
            applyConfiguration();
        } catch ( NexusException e ) {
            e.printStackTrace();
        }
    }

    /**
     * @param partner
     */
    public void deletePartner( PartnerPojo partner ) {

        try {
            PartnerPojo oldPartner = getPartnerByNxPartnerId( partner.getNxPartnerId() );
            if ( oldPartner != null ) {
                getPartners( 0, null ).remove( oldPartner );
            }
            engineConfig.deletePartnerInDB( partner );
            applyConfiguration();
        } catch ( NexusException e ) {
            e.printStackTrace();
        }
    }

    /**
     * @param component
     */
    public void updateComponent( ComponentPojo component ) {

        try {
            ComponentPojo oldComponent = getComponentByNxComponentId( component.getNxComponentId() );
            if ( oldComponent != null ) {
                getComponents( ComponentType.ALL, null ).remove( oldComponent );
            }
            getComponents( ComponentType.ALL, null ).add( component );

            //engineConfig.saveConfigurationToDB();
            applyConfiguration();
        } catch ( NexusException e ) {
            e.printStackTrace();
        }
    }

    /**
     * @param component
     */
    public void deleteComponent( ComponentPojo component ) {

        try {
            ComponentPojo oldComponent = getComponentByNxComponentId( component.getNxComponentId() );
            if ( oldComponent != null ) {
                getComponents( ComponentType.ALL, null ).remove( oldComponent );
            }
            engineConfig.deleteComponentInDB( component );
            applyConfiguration();
        } catch ( NexusException e ) {
            e.printStackTrace();
        }
    }

    /**
     * @param pipeline
     */
    public void updatePipeline( PipelinePojo pipeline ) {

        try {
            PipelinePojo oldPipeline = getPipelinePojoByNxPipelineId( pipeline.getNxPipelineId() );
            if ( oldPipeline != null ) {
                getBackendPipelinePojos( Constants.PIPELINE_TYPE_ALL, null ).remove( oldPipeline );
            }
            getBackendPipelinePojos( Constants.PIPELINE_TYPE_ALL, null ).add( pipeline );

            //engineConfig.saveConfigurationToDB();
            applyConfiguration();
        } catch ( NexusException e ) {
            e.printStackTrace();
        }
    }

    /**
     * @param pipeline
     */
    public void deletePipeline( PipelinePojo pipeline ) {

        try {
            PipelinePojo oldPipeline = getPipelinePojoByNxPipelineId( pipeline.getNxPipelineId() );
            if ( oldPipeline != null ) {
                getBackendPipelinePojos( Constants.PIPELINE_TYPE_ALL, null ).remove( oldPipeline );
            }
            engineConfig.deletePipelineInDB( pipeline );
            applyConfiguration();
        } catch ( NexusException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Convenience method that allows to  update a <code>Service</code>
     * without having to retrieve it's <code>ServicePojo</code> object
     * before. The update is performed through the service's unique name.
     * @param name The name for the service. If the name could not be
     * resolved, this method does nothing.
     */
    public void updateService( String name ) {

        Service service = getService( name );
        if ( service != null ) {
            for ( ServicePojo servicePojo : engineConfig.getServices() ) {
                if ( servicePojo.getName().equals( name ) ) {
                    servicePojo.setServiceParams( ConfigurationUtil.getConfiguration( service, servicePojo ) );
                    updateService( servicePojo );
                    break;
                }
            }
        }
    }

    /**
     * Updates or saves a service.
     * @param servicePojo The service to be updated or saved.
     */
    public void updateService( ServicePojo servicePojo ) {

        if ( servicePojo.getName() == null || servicePojo.getName().trim().length() == 0 ) {
            throw new IllegalArgumentException( "Service name must not be empty" );
        }

        try {
            List<ServicePojo> services = engineConfig.getServices();
            ServicePojo oldServicePojo = getServicePojoByNxServiceId( servicePojo.getNxServiceId() );
            Service service = null;
            if ( oldServicePojo != null ) {
                service = getService( oldServicePojo.getName() );
                services.remove( oldServicePojo );
                // service has been renamed
                if ( !oldServicePojo.getName().equals( servicePojo.getName() ) ) {
                    renameService( oldServicePojo.getName(), servicePojo.getName() );
                }
                if ( servicePojo.getServiceParams() != null ) {
                    ConfigurationUtil.configureService( service, servicePojo.getServiceParams() );
                }
            } else {
                service = (Service) Class.forName( servicePojo.getComponent().getClassName() ).newInstance();
                if ( servicePojo.getServiceParams() != null ) {
                    ConfigurationUtil.configureService( service, servicePojo.getServiceParams() );
                }
                service.initialize( engineConfig );
                service.activate();
                if ( service.isAutostart() ) {
                    service.start();
                }
                engineConfig.getStaticBeanContainer().getManagableBeans().put( servicePojo.getName(), service );
            }
            services.add( servicePojo );

            //engineConfig.saveConfigurationToDB();
            applyConfiguration();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a service.
     * @param servicePojo The service to be deleted.
     */
    public void deleteService( ServicePojo servicePojo ) {

        try {
            List<ServicePojo> services = engineConfig.getServices();
            ServicePojo oldServicePojo = getServicePojoByNxServiceId( servicePojo.getNxServiceId() );
            if ( oldServicePojo != null ) {
                Service oldService = getService( oldServicePojo.getName() );
                if ( oldService != null ) {
                    if ( oldService.getStatus() == BeanStatus.STARTED ) {
                        oldService.stop();
                    }
                    if ( oldService.getStatus() == BeanStatus.ACTIVATED ) {
                        oldService.deactivate();
                    }
                    getServiceInstances().remove( oldService );
                }
                Service service = getService( servicePojo.getName() );
                if ( service != null ) {
                    if ( service.getStatus() == BeanStatus.STARTED ) {
                        service.stop();
                    }
                    if ( service.getStatus() == BeanStatus.ACTIVATED ) {
                        service.deactivate();
                    }
                    engineConfig.getStaticBeanContainer().getManagableBeans().remove( servicePojo.getName() );
                }
                services.remove( oldServicePojo );
                engineConfig.deleteServiceInDB( oldServicePojo );
                applyConfiguration();
            }
        } catch ( NexusException e ) {
            e.printStackTrace();
        }
    }

    /**
     * @param loggerPojo
     */
    public void updateLogger( LoggerPojo loggerPojo ) {

        if ( loggerPojo.getName() == null || loggerPojo.getName().trim().length() == 0 ) {
            throw new IllegalArgumentException( "Logger name must not be empty" );
        }

        try {
            List<LoggerPojo> loggers = engineConfig.getLoggers();
            LoggerPojo oldLoggerPojo = getLoggerByNxLoggerId( loggerPojo.getNxLoggerId() );
            org.nexuse2e.logging.LogAppender logger = null;
            if ( oldLoggerPojo != null ) {
                logger = getLogger( oldLoggerPojo.getName() );
                loggers.remove( oldLoggerPojo );
                // service has been renamed
                if ( !oldLoggerPojo.getName().equals( loggerPojo.getName() ) ) {
                    renameLogger( oldLoggerPojo.getName(), loggerPojo.getName() );
                }
                if ( loggerPojo.getLoggerParams() != null ) {
                    ConfigurationUtil.configureLogger( logger, loggerPojo.getLoggerParams() );
                }
            } else {
                logger = (org.nexuse2e.logging.LogAppender) Class.forName( loggerPojo.getComponent().getClassName() )
                        .newInstance();
                if ( loggerPojo.getLoggerParams() != null ) {
                    ConfigurationUtil.configureLogger( logger, loggerPojo.getLoggerParams() );
                }
                logger.initialize( engineConfig );
                logger.activate();
                engineConfig.getStaticBeanContainer().getManagableBeans().put( loggerPojo.getName(), logger );
            }
            loggers.add( loggerPojo );

            //engineConfig.saveConfigurationToDB();
            applyConfiguration();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * @param logger
     */
    public void deleteLogger( LoggerPojo logger ) {

        try {
            LoggerPojo oldLogger = getLoggerByNxLoggerId( logger.getNxLoggerId() );
            if ( oldLogger != null ) {
                getLoggers().remove( oldLogger );
            }
            engineConfig.deleteLoggerInDB( logger );
            applyConfiguration();
        } catch ( NexusException e ) {
            e.printStackTrace();
        }
    }

    /**
     * @return
     */
    public List<LoggerPojo> getLoggers() {

        if(engineConfig != null) {
            return engineConfig.getLoggers();
        }
        return null;
    }

    /**
     * @param nxLoggerId
     * @return
     */
    public LoggerPojo getLoggerByNxLoggerId( int nxLoggerId ) {

        for ( LoggerPojo logger : engineConfig.getLoggers() ) {
            if ( logger.getNxLoggerId() == nxLoggerId ) {
                return logger;
            }
        }
        return null;
    }

    /**
     * Gets a logger by it's unique name.
     * @param name the logger name.
     * @return A <code>Logger</code> instance, or <code>null</code>
     * if no logger with the given name exists.
     */
    public org.nexuse2e.logging.LogAppender getLogger( String name ) {

        if(engineConfig != null) {
            return engineConfig.getStaticBeanContainer().getLogger( name );
        }
        return null;
    }

    /**
     * Renames a logger.
     * @param oldName The old logger name.
     * @param newName The new logger name.
     * @return The renamed logger. Never <code>null</code>.
     * @throws NexusException if <code>oldName</code> was not found or <code>newName</code>
     * already exists.
     */
    public org.nexuse2e.logging.LogAppender renameLogger( String oldName, String newName ) throws NexusException {

        if(engineConfig != null) {
            return engineConfig.getStaticBeanContainer().renameLogger( oldName, newName );
        }
        return null;
    }

    /**
     * Gets a service by it's unique name.
     * @param name the service name.
     * @return A <code>Service</code> instance, or <code>null</code>
     * if no service with the given name exists.
     */
    public Service getService( String name ) {

        if(engineConfig != null) {
            return engineConfig.getStaticBeanContainer().getService( name );
        }
        return null;
    }

    /**
     * Renames a service.
     * @param oldName The old service name.
     * @param newName The new service name.
     * @return The renamed service. Never <code>null</code>.
     * @throws NexusException if <code>oldName</code> was not found or <code>newName</code>
     * already exists.
     */
    public Service renameService( String oldName, String newName ) throws NexusException {

        if(engineConfig != null) {
            return engineConfig.getStaticBeanContainer().renameService( oldName, newName );
        }
        return null;
    }

    /**
     * Gets a list of all services that are registered.
     * @return A copied list of all services. Can be empty, but not <code>null</code>.
     */
    public List<Service> getServiceInstances() {

        if(engineConfig != null) {
            return engineConfig.getStaticBeanContainer().getServices();
        }
        return null;
    }

    /**
     * Gets a list of all services, represented as <code>ServicePojo</code>s.
     * @return A list of all services, as <code>ServicePojo</code> objects.
     */
    public List<ServicePojo> getServices() {

        if(engineConfig != null) {
            return engineConfig.getServices();
        }
        return null;
    }

    /**
     * @param pojo
     */
    public void deleteCertificate( int type, CertificatePojo certificate ) throws NexusException {

        CertificatePojo oldCertificate = getCertificateByNxCertificateId( type, certificate.getNxCertificateId() );
        if ( oldCertificate != null ) {
            getCertificates( Constants.CERTIFICATE_TYPE_ALL, null ).remove( oldCertificate );
        }
        engineConfig.deleteCertificateInDB( certificate );
        applyConfiguration();
    }

    /**
     * @param certificate
     */
    public void updateCertificate( CertificatePojo certificate ) throws NexusException {

        CertificatePojo oldCertificate = getCertificateByNxCertificateId( Constants.CERTIFICATE_TYPE_ALL, certificate
                .getNxCertificateId() );
        if ( oldCertificate != null ) {
            getCertificates( Constants.CERTIFICATE_TYPE_ALL, null );
        }

        getCertificates( Constants.CERTIFICATE_TYPE_ALL, null ).add( certificate );

        //engineConfig.saveConfigurationToDB();
        applyConfiguration();
    }

    /**
     * @return
     * @throws NexusException
     */
    public KeyStore getCacertsKeyStore() throws NexusException {

        try {
            CertificatePojo key = getFirstCertificateByType( Constants.CERTIFICATE_TYPE_CACERT_METADATA, true );
            String cacertspwd = "changeit";
            if ( key == null ) {
                key = new CertificatePojo();
                key.setName( "CaKeyStoreData" );
                key.setType( Constants.CERTIFICATE_TYPE_CACERT_METADATA );
                key.setBinaryData( new byte[0] );
                key.setPassword( EncryptionUtil.encryptString( cacertspwd ) );
                updateCertificate( key );
            } else {
                cacertspwd = EncryptionUtil.decryptString( key.getPassword() );
            }
            KeyStore keyStore = KeyStore.getInstance( "JKS" );
            keyStore.load( null, cacertspwd.toCharArray() );
            List certs = getCertificates( Constants.CERTIFICATE_TYPE_CA, null );
            Iterator certI = certs.iterator();
            // log.debug( "getCACertificates - count: " + certs.size() );
            while ( certI.hasNext() ) {
                CertificatePojo tempCert = (CertificatePojo) certI.next();
                byte[] data = tempCert.getBinaryData();
                if ( ( data != null ) && ( data.length != 0 ) ) {
                    try {
                        X509Certificate x509Certificate = CertificateUtil.getX509Certificate( data );
                        // log.debug( "cert: " + x509Certificate.getSubjectDN() + " - " + tempCert.getCertificateId() );
                        keyStore.setCertificateEntry( tempCert.getName(), x509Certificate );
                    } catch ( Exception e ) {
                        LOG.error( "Error importing certificate " + tempCert.getName() + ": " + e.getMessage() );
                    }
                }

            }
            return keyStore;
        } catch ( Exception e ) {
            LOG.error( "Error initializing Certificate store.  Exception:  " + e.getMessage() );
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns a sorted list of all users.
     * @param comparator The comperator to sort with.
     *          If <code>comparator</code> is <code>null</code> an unsorted list will be returned. 
     * @return a sorted list of all users.
     */
    public List<UserPojo> getUsers( Comparator<UserPojo> comparator ) {

        if(engineConfig == null) {
            return null;
        }
        List<UserPojo> users = engineConfig.getUsers();
        if ( comparator != null ) {
            Collections.sort( users, comparator );
        }
        return users;
    }

    /**
     * Returns the user with the given login name.
     * @param loginName login name of the desired user.
     * @return the user with the given <code>loginName</code> or <code>null</code>
     *          if no such user exists.
     */
    public UserPojo getUserByLoginName( String loginName ) {

        if ( loginName != null ) {
            for ( UserPojo user : engineConfig.getUsers() ) {
                if ( loginName.equals( user.getLoginName() ) ) {
                    return user;
                }
            }
        }
        return null;
    }

    /**
     * Returns the user with the given id.
     * @param nxUserId id of the desired user.
     * @return the user with the given <code>nxUserId</code> or <code>null</code>
     *          if no such user exists.
     */
    public UserPojo getUserByNxUserId( int nxUserId ) {
        
        if(engineConfig == null) {
            return null;
        }
        for ( UserPojo user : engineConfig.getUsers() ) {
            if ( nxUserId == user.getNxUserId() ) {
                return user;
            }
        }
        return null;
    }
    
    /**
     * Updates the given user or adds her to the list if she is new.
     * @param user User to add or update.
     */
    public void updateUser( UserPojo user ) {
        
        try {
            UserPojo oldUser = getUserByNxUserId( user.getNxUserId() );
            if ( oldUser != null ) {
                getUsers( null ).remove( oldUser );
            }
            getUsers( null ).add( user );
    
            applyConfiguration();
        } catch ( NexusException e ) {
            e.printStackTrace();
        }
    }
    
    /**
     * Deletes the given user.
     * @param user User to delete.
     */
    public void deleteUser( UserPojo user ) {

        try {
            UserPojo oldUser = getUserByNxUserId( user.getNxUserId() );
            if ( oldUser!= null ) {
                getUsers( null ).remove( oldUser );
            }
            engineConfig.deleteUserInDB( user );
            applyConfiguration();
        } catch ( NexusException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a sorted list of all roles.
     * @param comparator The comperator to sort with.
     * @return a sorted list of all roles.
     */
    public List<RolePojo> getRoles( Comparator<RolePojo> comparator ) {

        if(engineConfig == null) {
            return null;
        }
        List<RolePojo> roles = engineConfig.getRoles();
        if ( comparator != null ) {
            Collections.sort( roles, comparator );
        }
        return roles;
    }

    /**
     * Returns the role with the given id.
     * @param nxRoleId id of the desired role.
     * @return the role with the given <code>nxRoleId</code> or <code>null</code>
     *          if no such role exists.
     */
    public RolePojo getRoleByNxRoleId( int nxRoleId ) {

        for ( RolePojo role : engineConfig.getRoles() ) {
            if ( nxRoleId == role.getNxRoleId() ) {
                return role;
            }
        }
        return null;
    }
    
    /**
     * Returns the role with the given <code>name</code>.
     * @param name name of the desired role.
     * @return the role with the given <code>name</code> or <code>null</code>
     *          if no such role exists.
     */
    public RolePojo getRoleByName( String name ) {
        if ( name != null ) {
            for ( RolePojo role : engineConfig.getRoles() ) {
                if ( name.equals( role.getName() ) ) {
                    return role;
                }
            }
        }
        return null;
    }
    
    /**
     * Updates the given role or adds it to the list if it is new.
     * @param role Role to add or update.
     */
    public void updateRole( RolePojo role ) {
        
        try {
            RolePojo oldRole = getRoleByNxRoleId( role.getNxRoleId() );
            if ( oldRole != null ) {
                getRoles( null ).remove( oldRole );
            }
            getRoles( null ).add( role );
    
            applyConfiguration();
        } catch ( NexusException e ) {
            e.printStackTrace();
        }
    }
    
    /**
     * Deletes the given role.
     * @param role Role to delete.
     */
    public void deleteRole( RolePojo role ) {

        try {
            RolePojo oldRole = getRoleByNxRoleId( role.getNxRoleId() );
            if ( oldRole!= null ) {
                getRoles( null ).remove( oldRole );
            }
            engineConfig.deleteRoleInDB( role );
            applyConfiguration();
        } catch ( NexusException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Applies the current configuration and updates the engine.
     * @throws NexusException if the configuration update failed.
     */
    public void applyConfiguration() throws NexusException {

        // TODO: swap configurations
        Engine.getInstance().setCurrentConfiguration( engineConfig );
        // update menu tree
        CachedXmlStructureServer cachedStructureServer = (CachedXmlStructureServer) Engine.getInstance().getBeanFactory().getBean( "structureService" );
        cachedStructureServer.clearStructureCache();
        
    }

    
}
