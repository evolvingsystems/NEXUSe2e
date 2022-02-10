package org.nexuse2e.reporting;

import org.nexuse2e.pojo.LogPojo;

import java.util.Date;

public class StatisticsEngineLog {
    private String className;
    private String methodName;
    private String severity;
    private String conversationId;
    private String description;
    private Date createdDate;

    public StatisticsEngineLog(LogPojo log) {
        this.className = log.getClassName();
        this.methodName = log.getMethodName();
        this.severity = getSeverityNameFromLevel(log.getSeverity());
        this.conversationId = log.getConversationId();
        this.description = log.getDescription();
        this.createdDate = log.getCreatedDate();
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    private String getSeverityNameFromLevel(int level) {
        switch (level) {
            case 600:
                return "TRACE";
            case 500:
                return "DEBUG";
            case 400:
                return "INFO";
            case 300:
                return "WARN";
            case 200:
                return "ERROR";
            case 100:
                return "FATAL";
            default:
                return null;
        }
    }
}
