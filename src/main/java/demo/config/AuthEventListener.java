package demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import demo.service.LoginAttemptService;

/**
 * Listens to Spring Security authentication events and delegates to
 * LoginAttemptService to record failures and clear counters on success.
 *
 * Failure tracking keys:
 *   - username  (blocks a specific account across all IPs)
 *   - client IP (blocks an IP regardless of which username it targets)
 *
 * Only AuthenticationFailureBadCredentialsEvent is counted — a
 * LockedException thrown by CustomUserDetailsService fires
 * AuthenticationFailureLockedEvent, which is intentionally ignored here
 * so the counter does not keep extending the lockout window.
 */
@Component
public class AuthEventListener {

    private static final Logger log = LoggerFactory.getLogger(AuthEventListener.class);

    private final LoginAttemptService loginAttemptService;

    public AuthEventListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @EventListener
    public void onFailure(AuthenticationFailureBadCredentialsEvent event) {
        String username = event.getAuthentication().getName();
        loginAttemptService.recordFailure(username);
        log.warn("[AuthEventListener] Bad credentials for username '{}'", username);

        String ip = extractIp(event.getAuthentication().getDetails());
        if (ip != null) {
            loginAttemptService.recordFailure(ip);
            log.warn("[AuthEventListener] Bad credentials from IP '{}'", ip);
        }
    }

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        loginAttemptService.recordSuccess(username);

        String ip = extractIp(event.getAuthentication().getDetails());
        if (ip != null) {
            loginAttemptService.recordSuccess(ip);
        }
    }

    private String extractIp(Object details) {
        if (details instanceof WebAuthenticationDetails webDetails) {
            return webDetails.getRemoteAddress();
        }
        return null;
    }
}
