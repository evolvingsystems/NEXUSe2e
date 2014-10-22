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
package org.nexuse2e.ui.form;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.nexuse2e.pojo.PartnerPojo;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author markus.breilmann
 */
public class MessageSubmissionForm implements Serializable {

    private static final long serialVersionUID = 9066165869803142603L;

    private String            choreographyId   = null;
    private String            actionId         = null;
    private int               receiver         = 0;
    private String            primaryKey       = null;
    private String            conversationId   = null;
    private boolean           sendFollowUp     = false;
    private int               repeat           = 1;
    private CommonsMultipartFile payloadFile1     = null;
    private CommonsMultipartFile payloadFile2     = null;
    private CommonsMultipartFile payloadFile3     = null;

    private Collection<String>       choreographies   = null;
    private Collection<String>       actions          = null;
    private Collection<PartnerPojo>  receivers        = null;
    private List<String>             encodings        = null;
    

    public String getActionId() {

        return actionId;
    }

    public void setActionId( String action ) {

        this.actionId = action;
    }

    public String getChoreographyId() {

        return choreographyId;
    }

    public void setChoreographyId( String choreographyId ) {

        this.choreographyId = choreographyId;
    }

    public String getConversationId() {

        return conversationId;
    }

    public void setConversationId( String conversationId ) {

        this.conversationId = conversationId;
    }
    
    public boolean isSendFollowUp() {

        return sendFollowUp;
    }

    public void setSendFollowUp( boolean sendFollowUp ) {

        this.sendFollowUp = sendFollowUp;
    }

    public String getPrimaryKey() {

        return primaryKey;
    }

    public void setPrimaryKey( String primaryKey ) {

        this.primaryKey = primaryKey;
    }

    public int getReceiver() {

        return receiver;
    }

    public void setReceiver( int receiver ) {

        this.receiver = receiver;
    }

    public int getRepeat() {

        return repeat;
    }

    public void setRepeat( int repeat ) {

        this.repeat = repeat;
    }

    public Collection<String> getActions() {

        return actions;
    }

    public void setActions( Collection<String> actions ) {

        this.actions = actions;
    }

    public Collection<String> getChoreographies() {

        return choreographies;
    }

    public void setChoreographies( Collection<String> choreographies ) {

        this.choreographies = choreographies;
    }

    public Collection<PartnerPojo> getReceivers() {

        return receivers;
    }

    public void setReceivers( Collection<PartnerPojo> receivers ) {

        this.receivers = receivers;
    }

    public CommonsMultipartFile getPayloadFile1() {

        return payloadFile1;
    }

    public void setPayloadFile1( CommonsMultipartFile payloadFile1 ) {

        this.payloadFile1 = payloadFile1;
    }

    public CommonsMultipartFile getPayloadFile2() {

        return payloadFile2;
    }

    public void setPayloadFile2( CommonsMultipartFile payloadFile2 ) {

        this.payloadFile2 = payloadFile2;
    }

    public CommonsMultipartFile getPayloadFile3() {

        return payloadFile3;
    }

    public void setPayloadFile3( CommonsMultipartFile payloadFile3 ) {

        this.payloadFile3 = payloadFile3;
    }

    public List<String> getEncodings() {

        return encodings;
    }

    public void setEncodings( List<String> encodings ) {

        this.encodings = encodings;
    }
    
}
