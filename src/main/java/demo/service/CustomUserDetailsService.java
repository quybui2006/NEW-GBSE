package demo.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import demo.model.User;
import demo.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;

    public CustomUserDetailsService(UserRepository userRepository,
                                    LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // ── Brute-force check: username ───────────────────────────────────────
        if (loginAttemptService.isBlocked(username)) {
            log.warn("[Auth] Login blocked — username '{}' is temporarily locked.", username);
            throw new LockedException("Account temporarily locked. Try again in 10 minutes.");
        }

        // ── Brute-force check: client IP ──────────────────────────────────────
        String ip = resolveClientIp();
        if (ip != null && loginAttemptService.isBlocked(ip)) {
            log.warn("[Auth] Login blocked — IP '{}' is temporarily locked.", ip);
            throw new LockedException("Too many failed attempts. Try again in 10 minutes.");
        }

        // ── Normal authentication ─────────────────────────────────────────────
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            log.warn("Login attempt for unknown username: {}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }

        User user = userOpt.get();

        if (!user.isEnabled()) {
            log.warn("Login attempt for disabled account: {}", username);
        }

        // Use the role stored in the database — never hardcode a role here.
        // DataInitializer stores "ADMIN"; .roles() prepends "ROLE_" automatically.
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .disabled(!user.isEnabled())
                .build();
    }

    /** Returns the client IP from the current request context, or null if unavailable. */
    private String resolveClientIp() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return attrs.getRequest().getRemoteAddr();
        } catch (IllegalStateException e) {
            // Called outside a web request context (e.g., during startup or tests).
            return null;
        }
    }
}
