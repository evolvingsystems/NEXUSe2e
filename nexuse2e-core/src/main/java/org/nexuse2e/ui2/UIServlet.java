package org.nexuse2e.ui2;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

public class UIServlet extends HttpServlet {

    private static final String BASE = "/WEB-INF/ui/";
    private static final String ENTRY_POINT = "index.html";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getPathInfo();
        String outputFilename = ENTRY_POINT;
        if (path != null) {
            if (!FilenameUtils.getExtension(path).isEmpty()) {
                outputFilename = FilenameUtils.getName(path);
            }
        }
        handleResponse(response, outputFilename);
    }

    private void handleResponse(HttpServletResponse response, String fileName) throws IOException {
        try (InputStream in = getServletContext().getResourceAsStream(BASE + fileName)) {
            if (in == null) {
                response.setStatus(404);
                return;
            }
            String mimeType = getMimeTypeFromName(fileName);
            if (mimeType != null) {
                response.setContentType(mimeType);
            }
            ServletOutputStream out = response.getOutputStream();
            IOUtils.copy(in, out);
        }
    }

    private String getMimeTypeFromName(String filename) {
        switch (FilenameUtils.getExtension(filename)) {
            case "js": return "application/javascript";
            case "map": return "application/json";
            case "ico": return "image/x-icon";
            case "html": return "text/html";
            default: return null;
        }
    }

}
