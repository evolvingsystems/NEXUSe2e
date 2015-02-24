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

import javax.validation.constraints.Size;

import org.nexuse2e.pojo.ActionPojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ParticipantPojo;

/**
 * @author guido.esch
 */
public class ChoreographyForm implements Serializable {

    private static final long serialVersionUID = 6527987381436278179L;

    private int                       nxChoreographyId = 0;
    private String                    choreographyName;
    private String                    description;
    private String                    lastModifiedUserId;
    private Collection<ActionPojo>    actions;
    private Collection<ParticipantPojo> participants;

    public void setProperties(ChoreographyPojo choreographyPojo) {

        setNxChoreographyId(choreographyPojo.getNxChoreographyId());
        setChoreographyName(choreographyPojo.getName());
        setDescription(choreographyPojo.getDescription());
        setLastModifiedUserId("" + choreographyPojo.getModifiedNxUserId());
    }

    public ChoreographyPojo getProperties(ChoreographyPojo choreographyPojo) {

        choreographyPojo.setNxChoreographyId(getNxChoreographyId());
        choreographyPojo.setName(getChoreographyName());
        choreographyPojo.setDescription(getDescription());
        choreographyPojo.setModifiedNxUserId(new Integer(getLastModifiedUserId()).intValue());
        return choreographyPojo;
    }

    @Size(min = 1, message = "{choreography.error.name.required}")
    public String getChoreographyName() {
        return choreographyName;
    }

    public void setChoreographyName(String choreographyName) {
        this.choreographyName = choreographyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLastModifiedUserId() {
        return lastModifiedUserId;
    }

    public void setLastModifiedUserId(String lastModifiedUserId) {
        this.lastModifiedUserId = lastModifiedUserId;
    }

    public Collection<ActionPojo> getActions() {
        return actions;
    }

    public void setActions(Collection<ActionPojo> actions) {
        this.actions = actions;
    }

    public Collection<ParticipantPojo> getParticipants() {
        return participants;
    }

    public void setParticipants(Collection<ParticipantPojo> participants) {
        this.participants = participants;
    }

    public int getNxChoreographyId() {
        return nxChoreographyId;
    }

    public void setNxChoreographyId(int nxChoreographyId) {
        this.nxChoreographyId = nxChoreographyId;
    }
}
