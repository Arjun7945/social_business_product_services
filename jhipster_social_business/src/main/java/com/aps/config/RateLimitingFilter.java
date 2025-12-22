package com.aps.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

/**
 * Simple in-memory Rate Limiting Filter using Bucket4j.
 * Applies a limit of 20 requests per minute per IP for sensitive endpoints.
 */
@Component
@Order(1)
public class RateLimitingFilter implements Filter {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    // Limit: 20 requests per minute
    private Bandwidth limit = Bandwidth.classic(20, Refill.greedy(20, Duration.ofMinutes(1)));

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        String path = httpServletRequest.getRequestURI();

        // Apply only to public/auth API paths, skip static assets/actuator if needed
        if (path.startsWith("/api")) {
            String ip = httpServletRequest.getRemoteAddr();
            Bucket bucket = cache.computeIfAbsent(ip, k -> Bucket.builder().addLimit(limit).build());

            if (bucket.tryConsume(1)) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                httpServletResponse.setStatus(429);
                httpServletResponse.getWriter().write("Too Many Requests");
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}
