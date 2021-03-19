package org.nexuse2e.ui2.rest;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class RestDispatcherServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(RestDispatcherServlet.class);

    private final List<Handler> handlers = new ArrayList<>();
    private final List<Handler> protectedHandlers = new ArrayList<>();

    public RestDispatcherServlet() {
        this.protectedHandlers.add(new UserHandler());
        this.protectedHandlers.add(new TransactionReportingHandler());
        this.handlers.add(new NoAnonymousAccessHandler(this.protectedHandlers));
        this.handlers.add(new LoginHandler());
        this.handlers.add(new ConfigHandler());
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) {
        for (Handler handler : handlers) {
            try {
                if (handler.canHandle(request.getPathInfo(), request.getMethod())) {
                    handler.handle(request, response);
                    return;
                }
            } catch (Exception e) {
                response.setStatus(500);
                LOG.error("Error occurred when handling path " + request.getPathInfo() + " with method " + request.getMethod());
                return;
            }
        }
        response.setStatus(404);
    }
}

