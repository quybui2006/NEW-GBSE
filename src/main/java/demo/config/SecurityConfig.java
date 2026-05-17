package demo.config;

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
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/products", "/products/**", "/product/**",
                                 "/contact", "/about", "/projects", "/news",
                                 "/css/**", "/js/**", "/images/**", "/uploads/**",
                                 "/error").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll()
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
          "script-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net; " +
          "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com https://cdn.jsdelivr.net; " +
          "font-src 'self' https://fonts.gstatic.com; " +
          "img-src 'self' data: /uploads/; " +
          "connect-src 'self'"
      ))
  );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
