package org.nexuse2e.ui2.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface Handler {
    boolean canHandle(String path, String method);

    void handle(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
