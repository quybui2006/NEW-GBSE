package demo.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AdminIpWhitelistFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AdminIpWhitelistFilter.class);

    private final List<String> allowedIps;

    public AdminIpWhitelistFilter(@Value("${admin.allowed-ip}") String allowedIpConfig) {
        this.allowedIps = Arrays.stream(allowedIpConfig.split(","))
                .map(String::trim)
                .toList();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String path = request.getServletPath();

        if (isProtectedPath(path)) {
            String clientIp = request.getRemoteAddr();
            if (!allowedIps.contains(clientIp)) {
                log.warn("Blocked access to {} from IP {}", path, clientIp);
                response.sendRedirect("/access-denied");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isProtectedPath(String path) {
        return path.equals("/login") || path.startsWith("/admin");
    }
}
