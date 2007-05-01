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
package org.nexuse2e.ui.structure.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.GenericComparator;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ParticipantPojo;
import org.nexuse2e.ui.structure.ParentalStructureNode;
import org.nexuse2e.ui.structure.StructureNode;
import org.nexuse2e.ui.structure.TargetProvider;

/**
 * @author mbreilmann
 *
 */
public class ParticipantTargetProvider implements TargetProvider {

    /* (non-Javadoc)
     * @see org.nexuse2e.ui.structure.TargetProvider#getStructure(org.nexuse2e.ui.structure.StructureNode)
     */
    @SuppressWarnings("unchecked")
    public List<StructureNode> getStructure( StructureNode pattern, ParentalStructureNode parent ) {

        List<StructureNode> list = new ArrayList<StructureNode>();

        try {
            ChoreographyPojo choreographyPojo = Engine.getInstance().getActiveConfigurationAccessService()
                    .getChoreographyByNxChoreographyId( Integer.parseInt( parent.getProperty( "nxChoreographyId" ) ) );
            if ( choreographyPojo != null ) {
                List<ParticipantPojo> participants = choreographyPojo.getParticipants();
                TreeSet<ParticipantPojo> sortedParticipants = new TreeSet<ParticipantPojo>( new GenericComparator(
                        ParticipantPojo.class, "description", true ) );
                sortedParticipants.addAll( participants );
                for ( ParticipantPojo participantPojo : sortedParticipants ) {
                    StructureNode sn = new PageNode( pattern.getTarget() + "?nxPartnerId="
                            + participantPojo.getPartner().getNxPartnerId() + "&nxChoreographyId="
                            + choreographyPojo.getNxChoreographyId(), participantPojo.getPartner().getPartnerId() + " ("
                            + participantPojo.getPartner().getName() + ")", pattern.getIcon() );
                    list.add( sn );
                }

            }
        } catch ( NexusException e ) {
            e.printStackTrace();
        }

        return list;
    }

}
