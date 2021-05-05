package org.nexuse2e.ui2.rest;

import com.google.gson.Gson;
import org.nexuse2e.pojo.UserPojo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import static org.nexuse2e.ui.action.NexusE2EAction.ATTRIBUTE_USER;

public class UserHandler implements Handler {
    @Override
    public boolean canHandle(String path, String method) {
        return ("GET".equalsIgnoreCase(method) && "/full-username".equalsIgnoreCase(path)) ||
                ("GET".equalsIgnoreCase(method) && "/permission".equalsIgnoreCase(path));
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = request.getPathInfo();
        if (path != null) {
            switch (path) {
                case "/full-username":
                    getFullUsername(request, response);
                    break;
                case "/permission":
                    getPermission(request, response);
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

    private void getPermission(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // call AccessController.hasAccess
        response.getOutputStream().print(new Gson().toJson(true));

        /*
                UserPojo user = (UserPojo) request.getSession().getAttribute(NexusE2EAction.ATTRIBUTE_USER);
        if (user != null) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
         */
    }

}
