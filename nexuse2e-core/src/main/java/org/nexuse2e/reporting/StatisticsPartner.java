package org.nexuse2e.reporting;

import org.nexuse2e.pojo.PartnerPojo;

import java.util.List;

public class StatisticsPartner {
    private String partnerId;
    private int nxPartnerId;
    private String name;
    private String lastInboundTime;
    private String lastOutboundTime;
    private List<StatisticsCertificate> certificates;

    public StatisticsPartner(PartnerPojo partnerPojo) {
        this.partnerId = partnerPojo.getPartnerId();
        this.nxPartnerId = partnerPojo.getNxPartnerId();
        this.name = partnerPojo.getName();
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public int getNxPartnerId() {
        return nxPartnerId;
    }

    public void setNxPartnerId(int nxPartnerId) {
        this.nxPartnerId = nxPartnerId;
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

    public List<StatisticsCertificate> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<StatisticsCertificate> certificates) {
        this.certificates = certificates;
    }
}
