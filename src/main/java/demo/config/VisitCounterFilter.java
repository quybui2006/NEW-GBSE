package demo.config;

import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

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
    
    public static AtomicInteger totalVisits = new AtomicInteger(0);
    
    @Autowired
    private VisitLogRepository visitLogRepository;
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();
        
        // Chỉ đếm các request vào trang web (không đếm static resources và admin)
        if (!path.contains(".") && !path.startsWith("/admin")) {
            totalVisits.incrementAndGet();
            
            // Lưu log theo ngày
            try {
                LocalDate today = LocalDate.now();
                VisitLog visitLog = visitLogRepository.findByVisitDate(today)
                    .orElse(new VisitLog(today, 0));
                visitLog.setVisitCount(visitLog.getVisitCount() + 1);
                visitLogRepository.save(visitLog);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        chain.doFilter(request, response);
    }
}