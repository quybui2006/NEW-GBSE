package demo.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // ── CSRF ────────────────────────────────────────────────────────
            // HttpOnly=true (default) prevents JavaScript from reading the cookie.
            // All form submissions use the Thymeleaf-rendered token, so JS access
            // to the cookie is not required.
            .csrf(csrf -> csrf
                .csrfTokenRepository(new CookieCsrfTokenRepository())
                // Spring Security 6 stores a DeferredCsrfToken (Supplier) in the "_csrf"
                // request attribute instead of the actual CsrfToken. Templates that access
                // ${_csrf.parameterName} or ${_csrf.token} fail because Supplier has no
                // such properties. This handler eagerly resolves the token so templates work.
                .csrfTokenRequestHandler((request, response, deferredToken) -> {
                    CsrfToken token = deferredToken.get();
                    request.setAttribute("_csrf", token);
                    request.setAttribute(CsrfToken.class.getName(), token);
                })
            )

            // ── Authorization ────────────────────────────────────────────────
            // Explicit allowlist — every unmatched route is DENIED by default.
            // New controller methods are NOT accidentally public.
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/products", "/products/", "/products/**",
                                 "/contact", "/about", "/projects", "/news",
                                 "/css/**", "/js/**", "/images/**", "/uploads/**", "/docs/**",
                                 "/robots.txt", "/sitemap.xml", "/llms.txt", "/favicon.ico",
                                 "/actuator/health",
                                 "/error", "/access-denied").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().denyAll()
            )

            // ── Exception handling ───────────────────────────────────────────
            // Unauthenticated users hitting admin routes → login page.
            // Everything else (truly unknown routes) → 404 instead of a login redirect.
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    if (request.getRequestURI().startsWith("/admin")) {
                        response.sendRedirect(request.getContextPath() + "/login");
                    } else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    }
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    if (request.getRequestURI().startsWith("/admin")) {
                        response.sendRedirect(request.getContextPath() + "/access-denied");
                    } else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    }
                })
            )

            // ── Form login ───────────────────────────────────────────────────
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/admin", true)
                // Distinguish a locked account from bad credentials so the login page
                // can show a more helpful message. LockedException is thrown by
                // CustomUserDetailsService when LoginAttemptService reports a blocked key.
                .failureHandler((request, response, exception) -> {
                    String redirect = (exception instanceof LockedException)
                            ? "/login?locked=true"
                            : "/login?error=true";
                    response.sendRedirect(redirect);
                })
                .permitAll()
            )

            // ── Logout ───────────────────────────────────────────────────────
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            )

            // ── Security headers ─────────────────────────────────────────────
            // Spring Security already sets X-Content-Type-Options: nosniff and
            // Strict-Transport-Security by default. We override only what differs.
        .headers(headers -> headers
            .frameOptions(frame -> frame.sameOrigin())
            .referrerPolicy(referrer -> referrer
                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
            .contentSecurityPolicy(csp -> csp.policyDirectives(
  "default-src 'self'; " +
  "script-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://unpkg.com; " +
  "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com https://cdn.jsdelivr.net https://unpkg.com; " +
  "font-src 'self' https://fonts.gstatic.com https://unpkg.com; " +
  "img-src 'self' data: /uploads/ https://*.tile.openstreetmap.org https://unpkg.com; " +
  "connect-src 'self' https://*.tile.openstreetmap.org"
))
  );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
