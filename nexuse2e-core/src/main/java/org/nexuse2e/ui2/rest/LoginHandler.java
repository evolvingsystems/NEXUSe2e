package org.nexuse2e.ui2.rest;

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.UserPojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.util.PasswordUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

public class LoginHandler implements Handler {
    private static final Logger LOG = Logger.getLogger(LoginHandler.class);

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

    @Override
    public boolean canHandle(String path, String method) {
        return ("POST".equalsIgnoreCase(method) && "/login".equalsIgnoreCase(path)) ||
                ("POST".equalsIgnoreCase(method) && "/logout".equalsIgnoreCase(path)) ||
                ("GET".equalsIgnoreCase(method) && "/logged-in".equalsIgnoreCase(path));
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = request.getPathInfo();
        if (path != null) {
            switch (StringUtils.lowerCase(path)) {
                case "/login":
                    login(request, response);
                    break;
                case "/logged-in":
                    probeLogin(request, response);
                    break;
                case "/logout":
                    logout(request, response);
                    break;
            }
        }
    }

    private void probeLogin(HttpServletRequest request, HttpServletResponse response) {
        UserPojo user = (UserPojo) request.getSession().getAttribute(NexusE2EAction.ATTRIBUTE_USER);
        if (user != null) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
            response.setStatus(401);
        }
    }

    private void logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        session.removeAttribute(NexusE2EAction.ATTRIBUTE_USER);
        session.removeAttribute("patchManagementForm"); // remove patches
        response.setStatus(200);
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
        String user, password;
    }
}
