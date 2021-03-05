/**
 *  NEXUSe2e Business Messaging Open Source
 *  Copyright 2000-2021, direkt gruppe GmbH
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation version 3 of
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
package org.nexuse2e.reporting;

import org.nexuse2e.pojo.ChoreographyPojo;

public class StatisticsChoreography {
    private int nxChoreographyId;
    private String name;
    private String lastInboundTime;
    private String lastOutboundTime;

    public StatisticsChoreography(ChoreographyPojo choreographyPojo) {
        this.nxChoreographyId = choreographyPojo.getNxChoreographyId();
        this.name = choreographyPojo.getName();
    }

    public int getNxChoreographyId() {
        return nxChoreographyId;
    }

    public void setNxChoreographyId(int nxChoreographyId) {
        this.nxChoreographyId = nxChoreographyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastInboundTime() {
        return lastInboundTime;
    }

    public void setLastInboundTime(String lastInboundTime) {
        this.lastInboundTime = lastInboundTime;
    }

    public String getLastOutboundTime() {
        return lastOutboundTime;
    }

    public void setLastOutboundTime(String lastOutboundTime) {
        this.lastOutboundTime = lastOutboundTime;
    }
}
