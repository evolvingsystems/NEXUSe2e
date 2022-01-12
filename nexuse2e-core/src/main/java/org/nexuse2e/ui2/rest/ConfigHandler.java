/*
    You can find the API documentation in apiSpec.yaml file in
    \nexuse2e-core\src\main\javadoc\doc-files
 */

package org.nexuse2e.ui2.rest;

import com.google.gson.Gson;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nexuse2e.Version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ConfigHandler implements Handler {
    private static final Logger LOG = LogManager.getLogger(ConfigHandler.class);
    private static final String CONFIG_BASE = "/WEB-INF/config/";

    @Override
    public boolean canHandle(String path, String method) {
        return ("GET".equalsIgnoreCase(method) && "/machine-name".equalsIgnoreCase(path)) || ("GET".equalsIgnoreCase(method) && "/version".equalsIgnoreCase(path));
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = request.getPathInfo();
        if (path != null) {
            switch (path) {
                case "/machine-name":
                    getMachineName(request, response);
                    break;
                case "/version":
                    getVersion(response);
                    break;
            }
        }

    }

    private void getVersion(HttpServletResponse response) throws IOException {
        String version = Version.getVersion();
        response.getOutputStream().print(new Gson().toJson(version.split(", ")));
    }

    private void getMachineName(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String externalConfig = System.getProperty("externalconfig");
        if (StringUtils.isNotBlank(externalConfig)) {
            File file = new File(externalConfig, "machine_name.txt");
            try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
                String message = new Gson().toJson(br.readLine());
                response.getOutputStream().print(message);
            } catch (IOException e) {
                LOG.warn(e);
                response.sendError(404, "Machine Name not configured");
            }
        } else {
            try (InputStream in = request.getSession(true).getServletContext().getResourceAsStream(CONFIG_BASE +
                    "machine_name.txt"); InputStreamReader isr = new InputStreamReader(in); BufferedReader br =
                    new BufferedReader(isr)) {
                String message = new Gson().toJson(br.readLine());
                response.getOutputStream().print(message);
            }
        }

    }
}
