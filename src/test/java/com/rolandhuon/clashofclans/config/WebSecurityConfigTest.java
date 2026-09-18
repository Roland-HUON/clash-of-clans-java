package com.rolandhuon.clashofclans.config;

import com.rolandhuon.clashofclans.controller.PlayerController;
import com.rolandhuon.clashofclans.model.Player;
import com.rolandhuon.clashofclans.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlayerController.class)
@Import(WebSecurityConfig.class)
@DisplayName("the security chain")
class WebSecurityConfigTest {

    private static final String BODY = """
            {"name":"Probe","level":1,"gold":0,"elixir":0,"darkElixir":0}""";

    private static final RequestPostProcessor CHIEF = user("user").roles("USER");
    private static final RequestPostProcessor ADMIN = user("admin").roles("USER", "ADMIN");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlayerService playerService;

    private static RequestPostProcessor presentsASessionCookie() {
        return request -> {
            request.setRequestedSessionId(request.getSession(true).getId());
            return request;
        };
    }

    @BeforeEach
    void theServiceAlwaysSucceeds() {
        when(playerService.create(any())).thenReturn(mock(Player.class));
    }

    @Nested
    @DisplayName("a client with no credentials")
    class Anonymous {

        @Test
        @DisplayName("An API call is refused with a status, never a redirect to a login page.")
        void apiCallsAreRefusedWithAStatus() throws Exception {
            mockMvc.perform(get("/api/players"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(header().doesNotExist("Location"));
        }

        @Test
        @DisplayName("A browser is sent to the login page.")
        void browsersAreSentToTheLoginPage() throws Exception {
            mockMvc.perform(get("/village").accept(MediaType.TEXT_HTML))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/login.html"));
        }
    }

    @Nested
    @DisplayName("http basic")
    class Basic {

        @Test
        @DisplayName("The right password opens the API.")
        void theRightPasswordOpensTheApi() throws Exception {
            mockMvc.perform(get("/api/players").with(httpBasic("user", "password")))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("A wrong password is refused.")
        void aWrongPasswordIsRefused() throws Exception {
            mockMvc.perform(get("/api/players").with(httpBasic("user", "wrong")))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("A basic request needs no CSRF token: it carries its credentials itself.")
        void basicRequestsAreExemptFromCsrf() throws Exception {
            mockMvc.perform(post("/api/players")
                            .with(httpBasic("user", "password"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(BODY))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Garbage credentials are refused rather than waved through by the CSRF exemption.")
        void garbageCredentialsAreRefused() throws Exception {
            mockMvc.perform(post("/api/players")
                            .with(httpBasic("user", "wrong"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(BODY))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("a signed-in session")
    class Session {

        @Test
        @DisplayName("Reading is allowed.")
        void readingIsAllowed() throws Exception {
            mockMvc.perform(get("/api/players").with(CHIEF))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("A write without the CSRF token is refused.")
        void aWriteWithoutTheTokenIsRefused() throws Exception {
            mockMvc.perform(post("/api/players").with(CHIEF)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(BODY))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("A session write cannot switch CSRF off by bolting a Basic header onto itself.")
        void aSessionWriteCannotBuyItsWayOutOfCsrf() throws Exception {
            mockMvc.perform(post("/api/players")
                            .with(CHIEF)
                            .with(presentsASessionCookie())
                            .with(httpBasic("user", "WRONGPASS"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(BODY))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("A write carrying the CSRF token goes through.")
        void aWriteWithTheTokenGoesThrough() throws Exception {
            mockMvc.perform(post("/api/players").with(CHIEF).with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(BODY))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("deleting")
    class Deleting {

        @Test
        @DisplayName("A plain chief may not delete: they are known, just not allowed.")
        void aPlainChiefMayNotDelete() throws Exception {
            mockMvc.perform(delete("/api/players/1").with(CHIEF).with(csrf()))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("An admin may delete.")
        void anAdminMayDelete() throws Exception {
            mockMvc.perform(delete("/api/players/1").with(ADMIN).with(csrf()))
                    .andExpect(status().isNoContent());
        }
    }
}
