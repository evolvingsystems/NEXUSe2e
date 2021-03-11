package org.nexuse2e.ui2.rest;

import com.google.gson.Gson;
import org.nexuse2e.pojo.UserPojo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.nexuse2e.ui.action.NexusE2EAction.ATTRIBUTE_USER;

public class UserHandler implements Handler {
    @Override
    public boolean canHandle(String path, String method) {
        return ("GET".equalsIgnoreCase(method) && "/full-username".equalsIgnoreCase(path));
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        UserPojo user = (UserPojo) session.getAttribute(ATTRIBUTE_USER);
        String name = user.getFirstName() + " " + user.getLastName();
        response.getOutputStream().print(new Gson().toJson(name));
    }
}
