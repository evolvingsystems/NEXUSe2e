package org.nexuse2e.reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Statistics {

    private Date startDate;
    private Date endDAte;
    private List<MessageStub> messages = new ArrayList<>();


    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDAte() {
        return endDAte;
    }

    public void setEndDAte(Date endDAte) {
        this.endDAte = endDAte;
    }

    public List<MessageStub> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageStub> messages) {
        this.messages = messages;
    }

}
