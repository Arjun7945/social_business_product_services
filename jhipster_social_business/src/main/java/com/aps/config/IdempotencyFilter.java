package com.aps.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Idempotency Filter for critical POST requests.
 * Uses an in-memory map (for demo) or distributed cache (Production) to track
 * processed keys.
 */
@Component
@Order(2)
public class IdempotencyFilter implements Filter {

    private final Map<String, Boolean> processedKeys = Collections.synchronizedMap(new ConcurrentHashMap<>());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if ("POST".equalsIgnoreCase(httpRequest.getMethod()) && httpRequest.getRequestURI().contains("/api/customer-orders")) {
            String idempotencyKey = httpRequest.getHeader("Idempotency-Key");

            if (idempotencyKey != null && !idempotencyKey.isBlank()) {
                if (processedKeys.containsKey(idempotencyKey)) {
                    httpResponse.setStatus(409); // Conflict or 422
                    httpResponse.getWriter().write("Duplicate Request Detected");
                    return;
                }
                processedKeys.put(idempotencyKey, true);
            }
        }

        chain.doFilter(request, response);
    }
}
