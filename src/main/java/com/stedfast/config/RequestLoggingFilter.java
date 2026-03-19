package com.stedfast.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(wrappedRequest, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logRequest(wrappedRequest, response, duration);
        }
    }

    private void logRequest(ContentCachingRequestWrapper request, HttpServletResponse response, long duration) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        int status = response.getStatus();
        String remoteAddr = request.getRemoteAddr();

        String payload = getPayload(request);

        log.info("Request: {} {}{} | Status: {} | Duration: {}ms | Remote: {} | Payload: {}",
                method, uri, (queryString != null ? "?" + queryString : ""),
                status, duration, remoteAddr, payload);
    }

    private String getPayload(ContentCachingRequestWrapper request) {
        byte[] buf = request.getContentAsByteArray();
        if (buf.length > 0) {
            String payload = new String(buf, 0, buf.length, StandardCharsets.UTF_8);
            // Mask password if present in login/register requests
            if (request.getRequestURI().contains("/auth/")) {
                return "[PROTECTED]";
            }
            return payload.replaceAll("\\s+", " ");
        }
        return "[empty]";
    }
}
