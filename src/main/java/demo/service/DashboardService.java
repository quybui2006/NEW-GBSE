package demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        // Tạo datetime range
        LocalDateTime startDateTime = fromDate.atStartOfDay();
        LocalDateTime endDateTime = toDate.plusDays(1).atStartOfDay();
        
        // ✅ CÁCH 1: Dùng Native Query (khuyến nghị - hoạt động chắc chắn)
        List<YeuCau> filteredYeuCau = yeuCauRepository.findByDateRangeNative(startDateTime, endDateTime);
        
        // ✅ CÁCH 2: Nếu dùng cách tách method (bỏ comment 2 dòng dưới và comment dòng trên)
        // List<YeuCau> filteredYeuCau;
        // if (endDateTime != null) {
        //     filteredYeuCau = yeuCauRepository.findByThoiGianBetween(startDateTime, endDateTime);
        // } else {
        //     filteredYeuCau = yeuCauRepository.findByThoiGianGreaterThanEqual(startDateTime);
        // }
        
        // Lấy tất cả visit logs
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
    
    /**
     * Lấy dữ liệu dashboard có phân trang
     */
    public Map<String, Object> getDashboardDataWithPagination(String from, String to, int page, int size) {
        // Parse ngày
        LocalDate fromDate = parseDate(from, LocalDate.now().minusDays(6));
        LocalDate toDate = parseDate(to, LocalDate.now());

        // Đảo nếu from > to
        if (fromDate.isAfter(toDate)) {
            LocalDate temp = fromDate;
            fromDate = toDate;
            toDate = temp;
        }

        // Tạo datetime range
        LocalDateTime startDateTime = fromDate.atStartOfDay();
        LocalDateTime endDateTime = toDate.plusDays(1).atStartOfDay();
        
        // Lấy dữ liệu có phân trang
        var pageable = org.springframework.data.domain.PageRequest.of(page, size);
        var yeuCauPage = yeuCauRepository.findByDateRangeCoPhanTrang(startDateTime, endDateTime, pageable);
        
        // Lấy tất cả visit logs (không phân trang vì dữ liệu nhỏ)
        List<VisitLog> allVisitLogs = visitLogRepository.findAll();

        // Tính tổng
        long tongYeuCau = yeuCauPage.getTotalElements();
        long tongTruyCap = sumVisitsInRange(allVisitLogs, fromDate, toDate);
        boolean hasData = tongYeuCau > 0 || tongTruyCap > 0;

        // Tạo chart data
        List<String> labels = new ArrayList<>();
        List<Long> accessData = new ArrayList<>();
        List<Long> requestData = new ArrayList<>();

        for (LocalDate d = fromDate; !d.isAfter(toDate); d = d.plusDays(1)) {
            labels.add(d.format(DateTimeFormatter.ofPattern("dd/MM")));
            requestData.add(countRequestsOnDate(yeuCauPage.getContent(), d));
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
        result.put("yeuCauList", yeuCauPage.getContent());
        result.put("currentPage", yeuCauPage.getNumber());
        result.put("totalPages", yeuCauPage.getTotalPages());
        result.put("totalItems", yeuCauPage.getTotalElements());

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

    /**
     * Đếm số lượng yêu cầu trên một ngày cụ thể
     */
    private long countRequestsOnDate(List<YeuCau> list, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        return list.stream()
                .filter(yc -> yc.getThoiGian() != null
                        && !yc.getThoiGian().isBefore(start)
                        && yc.getThoiGian().isBefore(end))
                .count();
    }

    /**
     * Tính tổng số lượt truy cập trên một ngày cụ thể
     */
    private long sumVisitsOnDate(List<VisitLog> list, LocalDate date) {
        return list.stream()
                .filter(vl -> vl.getVisitDate() != null && vl.getVisitDate().equals(date))
                .mapToLong(VisitLog::getVisitCount)
                .sum();
    }

    /**
     * Tính tổng số lượt truy cập trong khoảng thời gian
     */
    private long sumVisitsInRange(List<VisitLog> list, LocalDate from, LocalDate to) {
        return list.stream()
                .filter(vl -> vl.getVisitDate() != null
                        && !vl.getVisitDate().isBefore(from)
                        && !vl.getVisitDate().isAfter(to))
                .mapToLong(VisitLog::getVisitCount)
                .sum();
    }
    
    /**
     * Lấy thống kê nhanh (cho phần header)
     */
    public Map<String, Object> getQuickStats() {
        Map<String, Object> stats = new HashMap<>();
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.withHour(0).withMinute(0).withSecond(0);
        
        // Thống kê hôm nay
        long yeuCauHomNay = yeuCauRepository.findByDateRangeNative(startOfDay, now).size();
        long truyCapHomNay = visitLogRepository.findByVisitDate(LocalDate.now())
                .map(VisitLog::getVisitCount)
                .orElse(0L);
        
        // Thống kê tuần này
        LocalDate startOfWeek = LocalDate.now().minusDays(6);
        long yeuCauTuanNay = yeuCauRepository.findByDateRangeNative(startOfWeek.atStartOfDay(), now).size();
        long truyCapTuanNay = sumVisitsInRange(visitLogRepository.findAll(), startOfWeek, LocalDate.now());
        
        stats.put("yeuCauHomNay", yeuCauHomNay);
        stats.put("truyCapHomNay", truyCapHomNay);
        stats.put("yeuCauTuanNay", yeuCauTuanNay);
        stats.put("truyCapTuanNay", truyCapTuanNay);
        stats.put("tongSanPham", sanPhamService.demTong());
        
        return stats;
    }
}