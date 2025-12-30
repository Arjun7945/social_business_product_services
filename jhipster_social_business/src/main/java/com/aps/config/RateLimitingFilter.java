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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Simple in-memory Rate Limiting Filter using Bucket4j.
 * Applies a limit of 20 requests per minute per IP for sensitive endpoints.
 */
@Component
@Order(1)
public class RateLimitingFilter implements Filter {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    @Value("${application.rate-limiting.enabled:true}")
    private boolean enabled;

    @Value("${application.rate-limiting.capacity:20}")
    private int capacity;

    @Value("${application.rate-limiting.duration-in-minutes:1}")
    private int durationInMinutes;

    private Bandwidth limit;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.limit = Bandwidth.classic(capacity, Refill.greedy(capacity, Duration.ofMinutes(durationInMinutes)));
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        String path = httpServletRequest.getRequestURI();

        // Apply only to public/auth API paths if enabled
        if (enabled && path.startsWith("/api")) {
            String ip = httpServletRequest.getRemoteAddr();
            // Re-build limit if configuration changes (though init handles startup)
            // For simplicity in this filter, we assume static config after startup or
            // simplistic map
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
