package org.nexuse2e.ui2.rest;

import com.google.gson.Gson;
import org.nexuse2e.pojo.UserPojo;
import org.nexuse2e.ui.action.NexusE2EAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.nexuse2e.ui.action.NexusE2EAction.ATTRIBUTE_USER;

public class UserHandler implements Handler {
    @Override
    public boolean canHandle(String path, String method) {
        return ("GET".equalsIgnoreCase(method) && "/full-username".equalsIgnoreCase(path)) ||
                ("GET".equalsIgnoreCase(method) && "/permitted-actions".equalsIgnoreCase(path));
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = request.getPathInfo();
        if (path != null) {
            switch (path) {
                case "/full-username":
                    getFullUsername(request, response);
                    break;
                case "/permitted-actions":
                    getPermittedActions(request, response);
                    break;
            }
        }
    }

    private void getFullUsername(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        UserPojo user = (UserPojo) session.getAttribute(ATTRIBUTE_USER);
        String name = user.getFirstName() + " " + user.getLastName();
        response.getOutputStream().print(new Gson().toJson(name));
    }

    private void getPermittedActions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserPojo user = (UserPojo) request.getSession().getAttribute(NexusE2EAction.ATTRIBUTE_USER);
        if (user != null) {
            Set<String> allowedRequests = user.getRole().getAllowedRequests().keySet();
            Set<String> permittedActionKeys = new HashSet<>();
            for (String path : allowedRequests) {
                permittedActionKeys.addAll(getActionKeys(path));
            }
            response.getOutputStream().print(new Gson().toJson(permittedActionKeys));
            return;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private Set<String> getActionKeys(String permissionKey) {
        Set<String> actionKeys = new HashSet<>();
        switch (permissionKey) {
            case "ModifyMessage.do":
                actionKeys.add("message.requeue");
                actionKeys.add("message.stop");
                actionKeys.add("message.delete");
                break;
            case "MessageView.do":
                actionKeys.add("messages.view");
                break;
            default:
                actionKeys.add(permissionKey);
        }
        return actionKeys;
    }

}
