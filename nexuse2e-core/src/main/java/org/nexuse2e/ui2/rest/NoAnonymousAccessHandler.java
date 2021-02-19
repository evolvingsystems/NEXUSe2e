package org.nexuse2e.ui2.rest;

import org.nexuse2e.ui.action.NexusE2EAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NoAnonymousAccessHandler implements Handler {
    private final Iterable<? extends Handler> handlers;

    public NoAnonymousAccessHandler(Iterable<? extends Handler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public boolean canHandle(String path, String method) {
        for (Handler handler : handlers) {
            if (handler.canHandle(path, method)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        if (req.getSession().getAttribute(NexusE2EAction.ATTRIBUTE_USER) != null) {
            for (Handler handler : handlers) {
                if (handler.canHandle(req.getPathInfo(), req.getMethod())) {
                    handler.handle(req, resp);
                }
            }
        } else {
            resp.setStatus(401);
        }
    }
}
