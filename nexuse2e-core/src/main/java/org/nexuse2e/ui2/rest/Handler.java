package org.nexuse2e.ui2.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Handler {
    boolean canHandle(String path, String method);

    void handle(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
