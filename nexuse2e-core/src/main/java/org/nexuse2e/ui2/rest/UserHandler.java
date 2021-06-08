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
                ("GET".equalsIgnoreCase(method) && "/allowed-endpoints".equalsIgnoreCase(path));
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = request.getPathInfo();
        if (path != null) {
            switch (path) {
                case "/full-username":
                    getFullUsername(request, response);
                    break;
                case "/allowed-endpoints":
                    getAllowedEndpoints(request, response);
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

    private void getAllowedEndpoints(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserPojo user = (UserPojo) request.getSession().getAttribute(NexusE2EAction.ATTRIBUTE_USER);
        if (user != null) {
            response.getOutputStream().print(new Gson().toJson(buildAllowedEndpointsList(user)));
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private static Set<String> buildAllowedEndpointsList(UserPojo user) {
        Set<String> allowedEndpoints = new HashSet<>();

        // endpoints that are allowed for all logged-in users
        allowedEndpoints.add("/full-username");
        allowedEndpoints.add("/allowed-endpoints");
        allowedEndpoints.add("/participants/ids");
        allowedEndpoints.add("/choreographies/ids");
        allowedEndpoints.add("/conversations");
        allowedEndpoints.add("/conversations/count");
        allowedEndpoints.add("/messages");
        allowedEndpoints.add("/messages/count");
        allowedEndpoints.add("/conversations-idle");
        allowedEndpoints.add("/engine-time-variables");
        allowedEndpoints.add("/conversation-status-counts");
        allowedEndpoints.add("/partners");
        allowedEndpoints.add("/choreographies");
        allowedEndpoints.add("/certificates-for-report");

        // endpoints that are allowed based on role
        Set<String> allowedRequests = user.getRole().getAllowedRequests().keySet();
        for (String path : allowedRequests) {
            allowedEndpoints.addAll(getAllowedEndpoints(path));
        }

        return allowedEndpoints;
    }

    public static boolean isUserPermitted(UserPojo user, String endpointPath) {
        if (user != null) {
            Set<String> permissions = buildAllowedEndpointsList(user);
            return permissions.contains("*") || permissions.contains(endpointPath);
        }
        return false;
    }

    private static Set<String> getAllowedEndpoints(String actionName) {
        Set<String> endpoints = new HashSet<>();
        switch (actionName) {
            case "ModifyMessage.do":
                endpoints.add("/messages/requeue");
                endpoints.add("/messages/stop");
                endpoints.add("/conversations/delete");
                break;
            default:
                endpoints.add(actionName);
        }
        return endpoints;
    }

}
