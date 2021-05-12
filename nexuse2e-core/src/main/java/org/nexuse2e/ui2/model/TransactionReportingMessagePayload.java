package org.nexuse2e.ui2.model;

public class TransactionReportingMessagePayload {
    private String mimeType;
    private String contentId;
    private int id;

    public TransactionReportingMessagePayload(String mimeType, String contentId, int id) {
        this.mimeType = mimeType;
        this.contentId = contentId;
        this.id = id;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
