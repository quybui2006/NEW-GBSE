package demo.config;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import demo.model.VisitLog;
import demo.repository.VisitLogRepository;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

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
                    log.incrementVisitCount();  // ✅ SỬA: dùng method helper
                    visitLogRepository.save(log);
                } else {
                    visitLogRepository.save(new VisitLog(today, 1L));  // ✅ SỬA: dùng 1L
                }
            } catch (Exception e) {
                // Không làm crash app nếu DB lỗi
            }
        }
        chain.doFilter(req, res);
    }
}