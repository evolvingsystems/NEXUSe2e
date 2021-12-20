package org.nexuse2e.ui2.rest;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class RestDispatcherServlet extends HttpServlet {

    private static final Logger LOG = LogManager.getLogger(RestDispatcherServlet.class);

    private final List<Handler> handlers = new ArrayList<>();

    public RestDispatcherServlet() {
        List<Handler> protectedHandlers = new ArrayList<>();
        protectedHandlers.add(new UserHandler());
        protectedHandlers.add(new TransactionReportingHandler());
        protectedHandlers.add(new DataManipulationHandler());
        this.handlers.add(new NoAnonymousAccessHandler(protectedHandlers));
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
                LOG.error("Error occurred when handling path " + request.getPathInfo() + " with method " + request.getMethod(), e);
                return;
            }
        }
        response.setStatus(404);
    }
}

