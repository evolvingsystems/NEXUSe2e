package org.nexuse2e.ui2.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.math.NumberUtils;
import org.nexuse2e.Engine;
import org.nexuse2e.dao.TransactionDAO;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.reporting.StatisticsMessage;
import org.nexuse2e.util.DateWithTimezoneSerializer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.nexuse2e.util.DateUtil.getCurrentDateMinusWeeks;

public class TransactionReportingHandler implements Handler {
    // TODO get date range from filter
    public static final Date TWO_WEEKS_AGO = getCurrentDateMinusWeeks(2);

    @Override
    public boolean canHandle(String path, String method) {
        return ("GET".equalsIgnoreCase(method) && "/messages".equalsIgnoreCase(path)) ||
                ("GET".equalsIgnoreCase(method) && "/messages-count".equalsIgnoreCase(path));
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if ("/messages".equalsIgnoreCase(request.getPathInfo())) {
            this.getMessages(request, response);
        } else {
            this.getMessagesCount(response);
        }
    }

    private void getMessagesCount(HttpServletResponse response) throws Exception {
        long messagesCount = Engine.getInstance().getTransactionService().getMessagesCount(TWO_WEEKS_AGO, new Date());
        String message = new Gson().toJson(messagesCount);
        response.getOutputStream().print(message);
    }

    private void getMessages(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String pageIndex = request.getParameter("pageIndex");
        String itemsPerPage = request.getParameter("itemsPerPage");
        if (NumberUtils.isNumber(pageIndex) && NumberUtils.isNumber(itemsPerPage)) {
            List<MessagePojo> reportMessages = Engine.getInstance().getTransactionService().getMessagesForReport(
                    null, 0, 0, null, null, null,
                    TWO_WEEKS_AGO,
                    new Date(),
                    Integer.parseInt(itemsPerPage),
                    Integer.parseInt(pageIndex),
                    TransactionDAO.SORT_CREATED,
                    false);

            List<StatisticsMessage> messages = new LinkedList<>();
            for (MessagePojo messagePojo : reportMessages) {
                messages.add(new StatisticsMessage(messagePojo));
            }

            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateWithTimezoneSerializer()).create();
            String messageJson = gson.toJson(messages);
            response.getOutputStream().print(messageJson);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Required parameters: pageIndex and itemsPerPage");
        }
    }
}
