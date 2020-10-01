package org.nexuse2e.rest;

import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.UserPojo;
import org.nexuse2e.ui.action.NexusE2EAction;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Writer;

public class AuthServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(AuthServlet.class);

    private static final String PATH_USER_NAME = "/getUserName";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String result = null;

        if (PATH_USER_NAME.equals(request.getPathInfo())) {
            UserPojo user = (UserPojo) request.getSession().getAttribute(NexusE2EAction.ATTRIBUTE_USER);
            if (user != null) {
                result = "{ \"user\" : \"" + user.getLoginName() + "\" }";
            }
        }

        if (result != null) {
            LOG.trace("Result: " + result);
            response.setContentType("text/json");
            response.setStatus(HttpServletResponse.SC_OK);
            Writer writer = response.getWriter();
            writer.write(result);
            writer.flush();
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        LOG.trace("RETURNING REQUEST");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EngineConfiguration engineConfig = Engine.getInstance().getCurrentConfiguration();
        UserPojo userInstance = engineConfig.getUserByLoginName("admin");
        if (userInstance != null) {
            HttpSession session = request.getSession();
            session.setAttribute(NexusE2EAction.ATTRIBUTE_USER, userInstance);
            response.setStatus(200);
        } else {
            super.doPost(request, response);
        }
    }
}

