package org.nexuse2e.ui2.rest;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.UserPojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

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
        String requestBody = readAll(request.getInputStream());

        Gson gson = new Gson();
        LoginData loginData = gson.fromJson(requestBody, LoginData.class);

        String password = "";
        try {
            password = PasswordUtil.hashPassword(loginData.password);
        } catch (NoSuchAlgorithmException e) {
            LOG.error(e);
        }

        UserPojo userInstance = engineConfig.getUserByLoginName(loginData.user);
        if (userInstance != null && constantTimeCompare(userInstance.getPassword(), password)) {
            HttpSession session = request.getSession();
            session.setAttribute(NexusE2EAction.ATTRIBUTE_USER, userInstance);
            response.setStatus(200);
        } else {
            super.doPost(request, response);
        }
    }

    private static String readAll(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader inr = new InputStreamReader(in, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(inr);
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    private byte[] parseHexString(final String s) {
        final int len = s.length();
        final byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }

        return data;
    }

    private boolean constantTimeCompare(final byte[] a, final byte[] b) {
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }

    private boolean constantTimeCompare(String a, String b) {
        return constantTimeCompare(parseHexString(a), parseHexString(b));
    }

    private static class LoginData {
        String user;
        String password;
    }
}

