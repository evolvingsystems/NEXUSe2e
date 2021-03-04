package org.nexuse2e.ui2.rest;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RestDispatcherServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(RestDispatcherServlet.class);

    private final List<Handler> handlers = new ArrayList<>();
    private final List<Handler> protectedHandlers = new ArrayList<>();

    public RestDispatcherServlet() {
        this.protectedHandlers.add(new UserHandler());
        this.handlers.add(new NoAnonymousAccessHandler(this.protectedHandlers));
        this.handlers.add(new LoginHandler());
        this.handlers.add(new ConfigHandler());
    }

    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        for (Handler handler : handlers) {
            try {
                if (handler.canHandle(req.getPathInfo(), req.getMethod())) {
                    handler.handle(req, resp);
                    return;
                }
            } catch (Exception e) {
                resp.setStatus(500);
                LOG.error("Error occurred when handling path " + req.getPathInfo() + " with method " + req.getMethod());
                return;
            }
        }
        resp.setStatus(404);
    }
}

