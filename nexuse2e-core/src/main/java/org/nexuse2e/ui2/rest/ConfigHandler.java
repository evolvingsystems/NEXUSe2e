package org.nexuse2e.ui2.rest;

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class ConfigHandler implements Handler {
    private static final Logger LOG = Logger.getLogger(ConfigHandler.class);
    private static final String CONFIG_BASE = "/WEB-INF/config/";

    @Override
    public boolean canHandle(String path, String method) {
        return ("GET".equalsIgnoreCase(method) && "/machine-name".equalsIgnoreCase(path));
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        getMachineName(req, resp);
    }

    private void getMachineName(HttpServletRequest req, HttpServletResponse response) throws IOException {
        String externalConfig = System.getProperty("externalconfig");
        if (StringUtils.isNotBlank(externalConfig)) {
            File file = new File(externalConfig, "machine_name.txt");
            try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
                String message = new Gson().toJson(br.readLine());
                response.getOutputStream().print(message);
            } catch (IOException e) {
                LOG.warn(e);
            }
        } else {
            try (InputStream in = req.getSession(true).getServletContext().getResourceAsStream(CONFIG_BASE + "machine_name.txt");
                 InputStreamReader isr = new InputStreamReader(in);
                 BufferedReader br = new BufferedReader(isr)) {
                String message = new Gson().toJson(br.readLine());
                response.getOutputStream().print(message);
            }
        }

    }
}
