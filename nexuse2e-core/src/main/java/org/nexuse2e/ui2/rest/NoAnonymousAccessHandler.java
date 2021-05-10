package org.nexuse2e.ui2.rest;

import org.nexuse2e.pojo.UserPojo;
import org.nexuse2e.ui.action.NexusE2EAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.nexuse2e.ui2.rest.UserHandler.isUserPermitted;

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
    public void handle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserPojo user = (UserPojo) request.getSession().getAttribute(NexusE2EAction.ATTRIBUTE_USER);
        if (isUserPermitted(user, request.getPathInfo())) {
            for (Handler handler : handlers) {
                if (handler.canHandle(request.getPathInfo(), request.getMethod())) {
                    handler.handle(request, response);
                }
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
