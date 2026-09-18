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
            long retryAfter = bucket.secondsUntilRefill(refillMillis);

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

        private int tokens = -1;
        private long windowStart = System.currentTimeMillis();

        synchronized boolean tryConsume(int capacity, long refillMillis) {
            long now = System.currentTimeMillis();

            if (tokens < 0 || now - windowStart >= refillMillis) {
                tokens = capacity;
                windowStart = now;
            }
            if (tokens == 0) {
                return false;
            }
            tokens--;
            return true;
        }

        synchronized int remaining() {
            return Math.max(0, tokens);
        }

        synchronized boolean isExpired(long refillMillis) {
            return System.currentTimeMillis() - windowStart >= refillMillis;
        }

        synchronized long secondsUntilRefill(long refillMillis) {
            long elapsed = System.currentTimeMillis() - windowStart;
            return Math.max(1, (refillMillis - elapsed + 999) / 1000);
        }
    }
}
