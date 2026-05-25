package demo.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
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

    // Prevent Spring Boot from auto-registering the filter in the servlet chain.
    // It is registered exclusively inside the Spring Security chain via addFilterBefore().
    @Bean
    public FilterRegistrationBean<AdminIpWhitelistFilter> ipWhitelistFilterRegistration(
            AdminIpWhitelistFilter filter) {
        FilterRegistrationBean<AdminIpWhitelistFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setEnabled(false);
        return reg;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // IP whitelist filter temporarily disabled — re-enable by:
        // 1. adding AdminIpWhitelistFilter ipWhitelistFilter back as a parameter
        // 2. uncommenting: .addFilterBefore(ipWhitelistFilter, UsernamePasswordAuthenticationFilter.class)
        // 3. updating admin.allowed-ip in Railway env vars with the correct static IP
        http
            // .addFilterBefore(ipWhitelistFilter, UsernamePasswordAuthenticationFilter.class)
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
                .requestMatchers("/", "/products", "/products/**",
                                 "/contact", "/about", "/projects", "/news",
                                 "/css/**", "/js/**", "/images/**", "/uploads/**",
                                 "/actuator/health",
                                 "/error", "/access-denied", "/debug/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().denyAll()
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
