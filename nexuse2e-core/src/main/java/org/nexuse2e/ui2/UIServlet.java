package org.nexuse2e.ui2;

import org.apache.commons.io.IOUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

public class UIServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getPathInfo();
        if (path != null && path.endsWith(".js")) {
            String[] segments = path.split("/");
            try (InputStream in = getServletContext().getResourceAsStream("/WEB-INF/ui/" + segments[segments.length - 1])) {
                if (in == null) {
                    response.setStatus(404);
                    return;
                }
                response.setContentType("application/javascript");
                ServletOutputStream out = response.getOutputStream();
                IOUtils.copy(in, out);
            }
        } else if (path != null && path.endsWith("favicon.ico")) {
            try (InputStream in = getServletContext().getResourceAsStream("/WEB-INF/ui/favicon.ico")) {
                if (in == null) {
                    response.setStatus(404);
                    return;
                }
                response.setContentType("image/x-icon");
                ServletOutputStream out = response.getOutputStream();
                IOUtils.copy(in, out);
            }
        } else {
            try (InputStream in = getServletContext().getResourceAsStream("/WEB-INF/ui/index.html")) {
                if (in == null) {
                    response.setStatus(404);
                    return;
                }
                response.setContentType("text/html");
                ServletOutputStream out = response.getOutputStream();
                IOUtils.copy(in, out);
            }
        }
    }

}
