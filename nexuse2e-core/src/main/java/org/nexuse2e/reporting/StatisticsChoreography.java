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
