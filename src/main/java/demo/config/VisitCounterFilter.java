package demo.config;

import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import demo.service.VisitService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class VisitCounterFilter implements Filter {

    public static final AtomicLong totalVisits = new AtomicLong(0);

    // Matches any path ending with a file extension (static assets).
    private static final Pattern STATIC_EXT = Pattern.compile(
        ".*\\.(css|js|png|jpg|jpeg|gif|webp|svg|ico|woff|woff2|ttf|eot|map|json)$",
        Pattern.CASE_INSENSITIVE
    );

    @Autowired
    private VisitService visitService;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        String path = request.getRequestURI();

        boolean isPageRequest = !path.startsWith("/admin")
                && !path.startsWith("/uploads")
                && !STATIC_EXT.matcher(path).matches();

        if (isPageRequest) {
            totalVisits.incrementAndGet();
            try {
                visitService.recordVisit(LocalDate.now());
            } catch (Exception e) {
                // Never let visit tracking crash the actual request.
            }
        }

        chain.doFilter(req, res);
    }
}
