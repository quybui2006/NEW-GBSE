package demo.config;

import demo.model.VisitLog;
import demo.repository.VisitLogRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class VisitCounterFilter implements Filter {

    public static final AtomicLong totalVisits = new AtomicLong(0);

    @Autowired
    private VisitLogRepository visitLogRepository;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        String path = request.getRequestURI();
        if (!path.startsWith("/admin") && !path.startsWith("/uploads")
                && !path.contains(".css") && !path.contains(".js")
                && !path.contains(".png") && !path.contains(".ico")) {
            totalVisits.incrementAndGet();
            try {
                LocalDate today = LocalDate.now();
                Optional<VisitLog> existing = visitLogRepository.findByVisitDate(today);
                if (existing.isPresent()) {
                    VisitLog log = existing.get();
                    log.setVisitCount(log.getVisitCount() + 1);
                    visitLogRepository.save(log);
                } else {
                    visitLogRepository.save(new VisitLog(today, 1));
                }
            } catch (Exception e) {
                // Không làm crash app nếu DB lỗi
            }
        }
        chain.doFilter(req, res);
    }
}