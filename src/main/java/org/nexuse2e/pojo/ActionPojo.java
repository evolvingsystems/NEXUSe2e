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
package org.nexuse2e.pojo;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

/**
 * ActionPojo generated by hbm2java
 */
@XmlType(name = "ActionType")
@XmlAccessorType(XmlAccessType.NONE)
public class ActionPojo implements NEXUSe2ePojo {

    /**
     * 
     */
    private static final long       serialVersionUID = 3011019828384391232L;

    // Fields    
    private int                     nxActionId;
    private ChoreographyPojo        choreography;
    private Date                    createdDate;
    private Date                    modifiedDate;
    private int                     modifiedNxUserId;
    private boolean                 start;
    private boolean                 end;
    private boolean                 pollingRequired;
    private PipelinePojo            inboundPipeline;
    private PipelinePojo            outboundPipeline;
    private PipelinePojo            statusUpdatePipeline;
    private String                  name;
    private String                  documentType;
    @XmlElementWrapper(name = "FollowUpActions")
    @XmlElement(name = "FollowUpAction")
    private Set<FollowUpActionPojo> followUpActions  = new HashSet<FollowUpActionPojo>( 0 );
    @XmlElementWrapper(name = "FollowedActions")
    @XmlElement(name = "FollowedAction")
    private Set<FollowUpActionPojo> followedActions  = new HashSet<FollowUpActionPojo>( 0 );

    private int                     statusUpdateNxPipelineId;
    private int                     inboundNxPipelineId;
    private int                     outboundNxPipelineId;

    // Constructors

    /** default constructor */
    public ActionPojo() {

        createdDate = new Date();
        modifiedDate = createdDate;
    }

    /** minimal constructor */
    public ActionPojo( ChoreographyPojo choreography, Date createdDate, Date modifiedDate, int modifiedNxUserId,
            boolean start, boolean end, PipelinePojo inboundPipeline, PipelinePojo outboundPipeline, String name ) {

        this.choreography = choreography;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.modifiedNxUserId = modifiedNxUserId;
        this.start = start;
        this.end = end;
        this.inboundPipeline = inboundPipeline;
        this.outboundPipeline = outboundPipeline;
        this.name = name;
    }

    /** full constructor */
    public ActionPojo( ChoreographyPojo choreography, Date createdDate, Date modifiedDate, int modifiedNxUserId,
            boolean start, boolean end, PipelinePojo inboundPipeline, PipelinePojo outboundPipeline, String name,
            Set<FollowUpActionPojo> followUpActions, Set<FollowUpActionPojo> followedActions ) {

        this.choreography = choreography;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.modifiedNxUserId = modifiedNxUserId;
        this.start = start;
        this.end = end;
        this.inboundPipeline = inboundPipeline;
        this.outboundPipeline = outboundPipeline;
        this.name = name;
        this.followUpActions = followUpActions;
        this.followedActions = followedActions;
    }

    // Property accessors
    @XmlAttribute
    public int getNxActionId() {

        return this.nxActionId;
    }

    public void setNxActionId( int nxActionId ) {

        this.nxActionId = nxActionId;
    }

    public int getNxId() {

        return nxActionId;
    }

    public void setNxId( int nxId ) {

        this.nxActionId = nxId;
    }

    public ChoreographyPojo getChoreography() {

        return this.choreography;
    }

    public void setChoreography( ChoreographyPojo choreography ) {

        this.choreography = choreography;
    }

    public Date getCreatedDate() {

        return this.createdDate;
    }

    public void setCreatedDate( Date createdDate ) {

        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {

        return this.modifiedDate;
    }

    public void setModifiedDate( Date modifiedDate ) {

        this.modifiedDate = modifiedDate;
    }

    public int getModifiedNxUserId() {

        return this.modifiedNxUserId;
    }

    public void setModifiedNxUserId( int modifiedNxUserId ) {

        this.modifiedNxUserId = modifiedNxUserId;
    }

    @XmlAttribute
    public boolean isStart() {

        return this.start;
    }

    public void setStart( boolean start ) {

        this.start = start;
    }

    @XmlAttribute
    public boolean isEnd() {

        return this.end;
    }

    public void setEnd( boolean end ) {

        this.end = end;
    }

    /**
     * Required for JAXB 
     * @return
     */
    @XmlAttribute
    public int getInboundNxPipelineId() {

        if ( this.inboundPipeline != null ) {
            return this.inboundPipeline.getNxPipelineId();

        }
        return inboundNxPipelineId;
    }

    /**
     * Required for JAXB
     * @param inboundPipelineId
     */
    public void setInboundNxPipelineId( int inboundPipelineId ) {

        this.inboundNxPipelineId = inboundPipelineId;
    }

    public PipelinePojo getInboundPipeline() {

        return this.inboundPipeline;
    }

    public void setInboundPipeline( PipelinePojo inboundPipeline ) {

        this.inboundPipeline = inboundPipeline;
    }

    /**
     * Required for JAXB 
     * @return
     */
    @XmlAttribute
    public int getOutboundNxPipelineId() {

        if ( this.outboundPipeline != null ) {
            return this.outboundPipeline.getNxPipelineId();

        }
        return outboundNxPipelineId;
    }

    /**
     * Required for JAXB
     * @param outboundPipelineId
     */
    public void setOutboundNxPipelineId( int outboundPipelineId ) {

        this.outboundNxPipelineId = outboundPipelineId;
    }

    public PipelinePojo getOutboundPipeline() {

        return this.outboundPipeline;
    }

    public void setOutboundPipeline( PipelinePojo outboundPipeline ) {

        this.outboundPipeline = outboundPipeline;
    }

    @XmlAttribute
    public String getName() {

        return this.name;
    }

    public void setName( String name ) {

        this.name = name;
    }

    public Set<FollowUpActionPojo> getFollowUpActions() {

        return this.followUpActions;
    }

    public void setFollowUpActions( Set<FollowUpActionPojo> followUpActions ) {

        this.followUpActions = followUpActions;
    }

    public Set<FollowUpActionPojo> getFollowedActions() {

        return this.followedActions;
    }

    public void setFollowedActions( Set<FollowUpActionPojo> followedActions ) {

        this.followedActions = followedActions;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {

        if ( !( obj instanceof ActionPojo ) ) {
            return false;
        }
        if ( nxActionId == 0 ) {
            return super.equals( obj );
        }

        // TODO Auto-generated method stub
        return nxActionId == ( (ActionPojo) obj ).nxActionId;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        if ( nxActionId == 0 ) {
            return super.hashCode();
        }

        return nxActionId;
    }

    /**
     * Required for JAXB 
     * @return
     */
    @XmlAttribute
    public int getStatusUpdateNxPipelineId() {

        if ( this.statusUpdatePipeline != null ) {
            return this.statusUpdatePipeline.getNxPipelineId();

        }
        return statusUpdateNxPipelineId;
    }

    /**
     * Required for JAXB
     * @param statusUpdatePipelineId
     */
    public void setStatusUpdateNxPipelineId( int statusUpdateNxPipelineId ) {

        this.statusUpdateNxPipelineId = statusUpdateNxPipelineId;
    }

    public PipelinePojo getStatusUpdatePipeline() {

        return statusUpdatePipeline;
    }

    public void setStatusUpdatePipeline( PipelinePojo statusUpdatePipeline ) {

        this.statusUpdatePipeline = statusUpdatePipeline;
    }

    /**
     * Required for JAXB
     * @param statusUpdatePipelineId
     */
    @XmlAttribute
    public boolean isPollingRequired() {

        return pollingRequired;
    }

    public void setPollingRequired( boolean pollingRequired ) {

        this.pollingRequired = pollingRequired;
    }

    /**
     * Required for JAXB
     * @param statusUpdatePipelineId
     */
    @XmlAttribute
    public String getDocumentType() {

        return documentType;
    }

    public void setDocumentType( String documentType ) {

        this.documentType = documentType;
    }

}
