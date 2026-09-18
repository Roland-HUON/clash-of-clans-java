package com.rolandhuon.clashofclans.web;

import com.rolandhuon.clashofclans.config.RateLimitProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RateLimitFilterTest {

    private static final int CAPACITY = 3;

    private RateLimitFilter filter(boolean enabled) {
        return new RateLimitFilter(new RateLimitProperties(enabled, CAPACITY, 60, 10_000, false));
    }

    private RateLimitFilter behindAProxy() {
        return new RateLimitFilter(new RateLimitProperties(true, CAPACITY, 60, 10_000, true));
    }

    private MockHttpServletRequest request(String uri, String clientIp) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", uri);
        request.setRemoteAddr(clientIp);
        return request;
    }

    private MockHttpServletResponse call(RateLimitFilter filter, MockHttpServletRequest request) throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
        return response;
    }

    @Test
    @DisplayName("Requests inside the quota go through and advertise what is left.")
    void requestsInsideTheQuotaGoThrough() throws Exception {
        RateLimitFilter filter = filter(true);

        for (int i = 1; i <= CAPACITY; i++) {
            MockHttpServletResponse response = call(filter, request("/api/players", "10.0.0.1"));

            assertThat(response.getStatus()).as("request %d", i).isEqualTo(200);
            assertThat(response.getHeader("X-RateLimit-Limit")).isEqualTo(String.valueOf(CAPACITY));
            assertThat(response.getHeader("X-RateLimit-Remaining")).isEqualTo(String.valueOf(CAPACITY - i));
        }
    }

    @Test
    @DisplayName("One request too many is answered with 429 and a Retry-After.")
    void oneRequestTooManyIsRejected() throws Exception {
        RateLimitFilter filter = filter(true);
        for (int i = 0; i < CAPACITY; i++) call(filter, request("/api/players", "10.0.0.1"));

        MockHttpServletResponse response = call(filter, request("/api/players", "10.0.0.1"));

        assertThat(response.getStatus()).isEqualTo(429);
        assertThat(response.getContentType()).startsWith("application/json");
        assertThat(response.getHeader("Retry-After")).isNotNull();
        assertThat(response.getHeader("X-RateLimit-Remaining")).isEqualTo("0");
        assertThat(response.getContentAsString()).contains("429");
    }

    @Test
    @DisplayName("The quota is counted per client, not globally.")
    void theQuotaIsCountedPerClient() throws Exception {
        RateLimitFilter filter = filter(true);
        for (int i = 0; i < CAPACITY; i++) call(filter, request("/api/players", "10.0.0.1"));

        MockHttpServletResponse otherClient = call(filter, request("/api/players", "10.0.0.2"));

        assertThat(otherClient.getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("X-Forwarded-For is ignored unless the application is told it sits behind a proxy.")
    void theForwardedHeaderIsIgnoredByDefault() throws Exception {
        RateLimitFilter filter = filter(true);
        for (int i = 0; i < CAPACITY; i++) call(filter, request("/api/players", "10.0.0.1"));

        MockHttpServletRequest spoofed = request("/api/players", "10.0.0.1");
        spoofed.addHeader("X-Forwarded-For", "203.0.113.9");

        assertThat(call(filter, spoofed).getStatus())
                .as("a client must not escape its quota by setting a header")
                .isEqualTo(429);
    }

    @Test
    @DisplayName("Behind a declared proxy the client is read from X-Forwarded-For.")
    void theForwardedClientIsUsedBehindAProxy() throws Exception {
        RateLimitFilter filter = behindAProxy();

        for (int i = 0; i < CAPACITY; i++) {
            MockHttpServletRequest request = request("/api/players", "10.0.0.1");
            request.addHeader("X-Forwarded-For", "203.0.113.7, 10.0.0.254");
            call(filter, request);
        }

        MockHttpServletRequest otherClient = request("/api/players", "10.0.0.1");
        otherClient.addHeader("X-Forwarded-For", "203.0.113.8, 10.0.0.254");

        assertThat(call(filter, otherClient).getStatus())
                .as("both clients share the same proxy address but not the same quota")
                .isEqualTo(200);
    }

    @Test
    @DisplayName("Anything outside /api is never throttled.")
    void nonApiRequestsAreNeverThrottled() throws Exception {
        RateLimitFilter filter = filter(true);

        for (int i = 0; i < CAPACITY * 3; i++) {
            MockHttpServletResponse response = call(filter, request("/swagger-ui/index.html", "10.0.0.1"));

            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getHeader("X-RateLimit-Limit")).isNull();
        }
    }

    @Test
    @DisplayName("Turning the limit off lets everything through.")
    void aDisabledLimitLetsEverythingThrough() throws Exception {
        RateLimitFilter filter = filter(false);

        for (int i = 0; i < CAPACITY * 3; i++) {
            assertThat(call(filter, request("/api/players", "10.0.0.1")).getStatus()).isEqualTo(200);
        }
    }

    @Test
    @DisplayName("Impossible settings are refused instead of bricking the API.")
    void impossibleSettingsAreRefused() {
        assertThatThrownBy(() -> new RateLimitProperties(true, 0, 60, 10_000, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("capacity");

        assertThatThrownBy(() -> new RateLimitProperties(true, 60, 0, 10_000, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("refill-seconds");

        assertThatThrownBy(() -> new RateLimitProperties(true, 60, 60, 0, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("max-tracked-clients");
    }

    @Test
    @DisplayName("The bucket drips: a refused client gets a Retry-After it can act on.")
    void aRefusedClientIsToldWhenToComeBack() throws Exception {
        RateLimitFilter filter = filter(true);
        for (int i = 0; i < CAPACITY; i++) call(filter, request("/api/players", "10.0.0.9"));

        MockHttpServletResponse refused = call(filter, request("/api/players", "10.0.0.9"));

        assertThat(refused.getStatus()).isEqualTo(429);
        assertThat(Long.parseLong(refused.getHeader("Retry-After")))
                .as("one token of a %d-per-60s bucket is worth about %d seconds", CAPACITY, 60 / CAPACITY)
                .isBetween(1L, 60L);
    }
}
