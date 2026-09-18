package com.rolandhuon.clashofclans.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private static final String LOGIN_PAGE = "/login.html";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler csrf = new CsrfTokenRequestAttributeHandler();
        csrf.setCsrfRequestAttributeName(null);

        http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(LOGIN_PAGE, "/css/**", "/favicon.ico", "/error").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .csrf(it -> it
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(csrf)
                        .ignoringRequestMatchers(WebSecurityConfig::isStatelessBasicCall))
                .httpBasic(it -> it.securityContextRepository(new RequestAttributeSecurityContextRepository()))
                .requestCache(RequestCacheConfigurer::disable)
                .formLogin(it -> it
                        .loginPage(LOGIN_PAGE)
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl(LOGIN_PAGE + "?error")
                        .permitAll())
                .logout(it -> it.logoutSuccessUrl(LOGIN_PAGE + "?logout").permitAll())
                .exceptionHandling(it -> it.defaultAuthenticationEntryPointFor(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        PathPatternRequestMatcher.withDefaults().matcher("/api/**")));

        return http.build();
    }

    private static boolean isStatelessBasicCall(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Basic ")) return false;

        return request.getRequestedSessionId() == null || request.getSession(false) == null;
    }

    @Bean
    public UserDetailsService users(PasswordEncoder encoder) {
        UserDetails chief = User.withUsername("user")
                .password(encoder.encode("password"))
                .roles("USER")
                .build();

        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("admin"))
                .roles("USER", "ADMIN")
                .build();

        return new InMemoryUserDetailsManager(chief, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
