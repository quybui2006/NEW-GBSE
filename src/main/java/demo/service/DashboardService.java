package demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import demo.model.VisitLog;
import demo.model.YeuCau;
import demo.repository.VisitLogRepository;
import demo.repository.YeuCauRepository;

@Service
public class DashboardService {

    @Autowired
    private YeuCauRepository yeuCauRepository;

    @Autowired
    private VisitLogRepository visitLogRepository;

    @Autowired
    private SanPhamService sanPhamService;

    /**
     * Lấy toàn bộ dữ liệu thống kê cho dashboard
     */
    public Map<String, Object> getDashboardData(String from, String to) {
        // Parse ngày
        LocalDate fromDate = parseDate(from, LocalDate.now().minusDays(6));
        LocalDate toDate = parseDate(to, LocalDate.now());

        // Đảo nếu from > to
        if (fromDate.isAfter(toDate)) {
            LocalDate temp = fromDate;
            fromDate = toDate;
            toDate = temp;
        }

        // ✅ SỬA: Load dữ liệu đã lọc từ database (không findAll() toàn bộ)
        LocalDateTime startDateTime = fromDate.atStartOfDay();
        LocalDateTime endDateTime = toDate.plusDays(1).atStartOfDay();
        List<YeuCau> filteredYeuCau = yeuCauRepository.findByDateRange(startDateTime, endDateTime);
        List<VisitLog> allVisitLogs = visitLogRepository.findAll();

        // Tính tổng
        long tongYeuCau = filteredYeuCau.size();
        long tongTruyCap = sumVisitsInRange(allVisitLogs, fromDate, toDate);
        boolean hasData = tongYeuCau > 0 || tongTruyCap > 0;

        // Tạo chart data
        List<String> labels = new ArrayList<>();
        List<Long> accessData = new ArrayList<>();
        List<Long> requestData = new ArrayList<>();

        for (LocalDate d = fromDate; !d.isAfter(toDate); d = d.plusDays(1)) {
            labels.add(d.format(DateTimeFormatter.ofPattern("dd/MM")));
            requestData.add(countRequestsOnDate(filteredYeuCau, d));
            accessData.add(sumVisitsOnDate(allVisitLogs, d));
        }

        // Đóng gói kết quả
        Map<String, Object> result = new HashMap<>();
        result.put("tongSanPham", sanPhamService.demTong());
        result.put("tongYeuCau", tongYeuCau);
        result.put("tongTruyCap", tongTruyCap);
        result.put("hasData", hasData);
        result.put("chartLabels", labels);
        result.put("chartAccessData", accessData);
        result.put("chartRequestData", requestData);
        result.put("fromDate", fromDate.toString());
        result.put("toDate", toDate.toString());

        return result;
    }

    // ========== HELPER METHODS ==========

    private LocalDate parseDate(String dateStr, LocalDate defaultDate) {
        try {
            return (dateStr != null && !dateStr.isEmpty()) ? LocalDate.parse(dateStr) : defaultDate;
        } catch (Exception e) {
            return defaultDate;
        }
    }

    // ✅ SỬA: Đếm trên dữ liệu đã lọc (nhẹ hơn)
    private long countRequestsOnDate(List<YeuCau> list, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        return list.stream()
                .filter(yc -> yc.getThoiGian() != null
                        && !yc.getThoiGian().isBefore(start)
                        && yc.getThoiGian().isBefore(end))
                .count();
    }

    private long sumVisitsOnDate(List<VisitLog> list, LocalDate date) {
        return list.stream()
                .filter(vl -> vl.getVisitDate() != null && vl.getVisitDate().equals(date))
                .mapToLong(VisitLog::getVisitCount)
                .sum();
    }

    private long sumVisitsInRange(List<VisitLog> list, LocalDate from, LocalDate to) {
        return list.stream()
                .filter(vl -> vl.getVisitDate() != null
                        && !vl.getVisitDate().isBefore(from)
                        && !vl.getVisitDate().isAfter(to))
                .mapToLong(VisitLog::getVisitCount)
                .sum();
    }
}