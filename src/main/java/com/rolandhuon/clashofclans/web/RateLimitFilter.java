package com.rolandhuon.clashofclans.web;

import com.rolandhuon.clashofclans.config.RateLimitProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitProperties properties;
    private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    private final TokenBucket overflow = new TokenBucket();

    public RateLimitFilter(RateLimitProperties properties) {
        this.properties = properties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !properties.enabled() || !request.getRequestURI().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        TokenBucket bucket = bucketFor(clientKey(request));
        long refillMillis = properties.refillSeconds() * 1000L;

        response.setHeader("X-RateLimit-Limit", String.valueOf(properties.capacity()));

        if (!bucket.tryConsume(properties.capacity(), refillMillis)) {
            long retryAfter = bucket.secondsUntilRefill(properties.capacity(), refillMillis);

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setHeader("X-RateLimit-Remaining", "0");
            response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(retryAfter));

            response.getWriter().write("{\"status\":429,\"message\":\"Rate limit exceeded: "
                    + properties.capacity() + " requests per " + properties.refillSeconds()
                    + "s. Retry in " + retryAfter + "s.\"}");
            return;
        }

        response.setHeader("X-RateLimit-Remaining", String.valueOf(bucket.remaining()));
        chain.doFilter(request, response);
    }

    private TokenBucket bucketFor(String key) {
        TokenBucket known = buckets.get(key);
        if (known != null) return known;

        if (buckets.size() >= properties.maxTrackedClients()) {
            buckets.values().removeIf(bucket -> bucket.isExpired(properties.refillSeconds() * 1000L));
        }
        if (buckets.size() >= properties.maxTrackedClients()) {
            return overflow;
        }
        return buckets.computeIfAbsent(key, k -> new TokenBucket());
    }

    private String clientKey(HttpServletRequest request) {
        if (properties.trustForwardedFor()) {
            String forwarded = request.getHeader("X-Forwarded-For");
            if (forwarded != null && !forwarded.isBlank()) {
                return forwarded.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }

    private static final class TokenBucket {

        private double tokens = -1;
        private long lastRefill = System.currentTimeMillis();

        synchronized boolean tryConsume(int capacity, long refillMillis) {
            refill(capacity, refillMillis);

            if (tokens < 1) return false;
            tokens -= 1;
            return true;
        }

        synchronized int remaining() {
            return (int) Math.max(0, Math.floor(tokens));
        }

        synchronized boolean isExpired(long refillMillis) {
            return System.currentTimeMillis() - lastRefill >= refillMillis;
        }

        synchronized long secondsUntilRefill(int capacity, long refillMillis) {
            double missing = Math.max(0, 1 - tokens);
            double millis = missing / perMilli(capacity, refillMillis);
            return Math.max(1, (long) Math.ceil(millis / 1000));
        }

        private void refill(int capacity, long refillMillis) {
            long now = System.currentTimeMillis();

            if (tokens < 0) {
                tokens = capacity;
                lastRefill = now;
                return;
            }

            tokens = Math.min(capacity, tokens + (now - lastRefill) * perMilli(capacity, refillMillis));
            lastRefill = now;
        }

        private static double perMilli(int capacity, long refillMillis) {
            return (double) capacity / Math.max(1, refillMillis);
        }
    }
}
