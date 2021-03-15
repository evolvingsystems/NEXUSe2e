package org.nexuse2e.ui2.rest;

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.nexuse2e.Engine;
import org.nexuse2e.dao.TransactionDAO;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.reporting.StatisticsConversation;
import org.nexuse2e.reporting.StatisticsMessage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.nexuse2e.util.DateUtil.getCurrentDateMinusWeeks;

public class TransactionReportingHandler implements Handler {
    @Override
    public boolean canHandle(String path, String method) {
        return ("GET".equalsIgnoreCase(method) && "/messages".equalsIgnoreCase(path)) ||
                ("GET".equalsIgnoreCase(method) && "/conversations".equalsIgnoreCase(path));
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = request.getPathInfo();
        if (path != null) {
            switch (StringUtils.lowerCase(path)) {
                case "/messages":
                    getMessages(request, response);
                    break;
                case "/conversations":
                    getConversations(request, response);
                    break;
            }
        }
    }

    private void getMessages(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // TODO get date range from filter
        List<MessagePojo> reportMessages = Engine.getInstance().getTransactionService()
                .getMessagesForReport(null, 0, 0, null, null, null,
                        getCurrentDateMinusWeeks(2), new Date(), 20, 0, TransactionDAO.SORT_CREATED, false);
        List<StatisticsMessage> messages = new LinkedList<>();
        for (MessagePojo messagePojo : reportMessages) {
            messages.add(new StatisticsMessage(messagePojo));
        }
        String messageJson = new Gson().toJson(messages);
        response.getOutputStream().print(messageJson);
    }

    private void getConversations(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // TODO get date range from filter
        List<ConversationPojo> reportConversations = Engine.getInstance().getTransactionService().getConversationsForReport(
                null, 0, 0, null, getCurrentDateMinusWeeks(2), new Date(),
                20, 0, TransactionDAO.SORT_CREATED, false);
        List<StatisticsConversation> conversations = new LinkedList<>();
        for (ConversationPojo conversationPojo : reportConversations) {
            conversations.add(new StatisticsConversation(conversationPojo));
        }
        String conversationJson = new Gson().toJson(conversations);
        response.getOutputStream().print(conversationJson);
    }
}
