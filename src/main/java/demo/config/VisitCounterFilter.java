package demo.config;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class VisitCounterFilter implements Filter {

    public static final AtomicLong totalVisits = new AtomicLong(0);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        String path = request.getRequestURI();
        if (!path.startsWith("/admin") && !path.startsWith("/uploads")
                && !path.contains(".css") && !path.contains(".js")) {
            totalVisits.incrementAndGet();
        }
        chain.doFilter(req, res);
    }
}