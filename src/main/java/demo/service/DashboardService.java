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

    public Map<String, Object> getDashboardData(String from, String to) {
        LocalDate fromDate = parseDate(from, LocalDate.now().minusDays(6));
        LocalDate toDate = parseDate(to, LocalDate.now());

        if (fromDate.isAfter(toDate)) {
            LocalDate temp = fromDate;
            fromDate = toDate;
            toDate = temp;
        }

        LocalDateTime startDateTime = fromDate.atStartOfDay();
        LocalDateTime endDateTime = toDate.plusDays(1).atStartOfDay();
        
        List<YeuCau> filteredYeuCau = yeuCauRepository.findByDateRangeNative(startDateTime, endDateTime);
        List<VisitLog> allVisitLogs = visitLogRepository.findAll();

        Long tongYeuCau = Long.valueOf(filteredYeuCau.size());
        Long tongTruyCap = Long.valueOf(sumVisitsInRange(allVisitLogs, fromDate, toDate));
        boolean hasData = filteredYeuCau.size() > 0 || sumVisitsInRange(allVisitLogs, fromDate, toDate) > 0;

        List<String> labels = new ArrayList<>();
        List<Long> accessData = new ArrayList<>();
        List<Long> requestData = new ArrayList<>();

        for (LocalDate d = fromDate; !d.isAfter(toDate); d = d.plusDays(1)) {
            labels.add(d.format(DateTimeFormatter.ofPattern("dd/MM")));
            requestData.add(countRequestsOnDate(filteredYeuCau, d));
            accessData.add(sumVisitsOnDate(allVisitLogs, d));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("tongSanPham", Long.valueOf(sanPhamService.demTong()));
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
    
    public Map<String, Object> getDashboardDataWithPagination(String from, String to, int page, int size) {
        LocalDate fromDate = parseDate(from, LocalDate.now().minusDays(6));
        LocalDate toDate = parseDate(to, LocalDate.now());

        if (fromDate.isAfter(toDate)) {
            LocalDate temp = fromDate;
            fromDate = toDate;
            toDate = temp;
        }

        LocalDateTime startDateTime = fromDate.atStartOfDay();
        LocalDateTime endDateTime = toDate.plusDays(1).atStartOfDay();
        
        var pageable = org.springframework.data.domain.PageRequest.of(page, size);
        var yeuCauPage = yeuCauRepository.findByDateRangeCoPhanTrang(startDateTime, endDateTime, pageable);
        List<VisitLog> allVisitLogs = visitLogRepository.findAll();

        Long tongYeuCau = Long.valueOf(yeuCauPage.getTotalElements());
        Long tongTruyCap = Long.valueOf(sumVisitsInRange(allVisitLogs, fromDate, toDate));
        boolean hasData = yeuCauPage.getTotalElements() > 0 || sumVisitsInRange(allVisitLogs, fromDate, toDate) > 0;

        List<String> labels = new ArrayList<>();
        List<Long> accessData = new ArrayList<>();
        List<Long> requestData = new ArrayList<>();

        for (LocalDate d = fromDate; !d.isAfter(toDate); d = d.plusDays(1)) {
            labels.add(d.format(DateTimeFormatter.ofPattern("dd/MM")));
            requestData.add(countRequestsOnDate(yeuCauPage.getContent(), d));
            accessData.add(sumVisitsOnDate(allVisitLogs, d));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("tongSanPham", Long.valueOf(sanPhamService.demTong()));
        result.put("tongYeuCau", tongYeuCau);
        result.put("tongTruyCap", tongTruyCap);
        result.put("hasData", hasData);
        result.put("chartLabels", labels);
        result.put("chartAccessData", accessData);
        result.put("chartRequestData", requestData);
        result.put("fromDate", fromDate.toString());
        result.put("toDate", toDate.toString());
        result.put("yeuCauList", yeuCauPage.getContent());
        result.put("currentPage", Integer.valueOf(yeuCauPage.getNumber()));
        result.put("totalPages", Integer.valueOf(yeuCauPage.getTotalPages()));
        result.put("totalItems", Long.valueOf(yeuCauPage.getTotalElements()));

        return result;
    }

    private LocalDate parseDate(String dateStr, LocalDate defaultDate) {
        try {
            return (dateStr != null && !dateStr.isEmpty()) ? LocalDate.parse(dateStr) : defaultDate;
        } catch (Exception e) {
            return defaultDate;
        }
    }

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
                .mapToLong(vl -> vl.getVisitCount().longValue())
                .sum();
    }

    private long sumVisitsInRange(List<VisitLog> list, LocalDate from, LocalDate to) {
        return list.stream()
                .filter(vl -> vl.getVisitDate() != null
                        && !vl.getVisitDate().isBefore(from)
                        && !vl.getVisitDate().isAfter(to))
                .mapToLong(vl -> vl.getVisitCount().longValue())
                .sum();
    }
    
    public Map<String, Object> getQuickStats() {
        Map<String, Object> stats = new HashMap<>();
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.withHour(0).withMinute(0).withSecond(0);
        
        Long yeuCauHomNay = Long.valueOf(yeuCauRepository.findByDateRangeNative(startOfDay, now).size());
        Long truyCapHomNay = Long.valueOf(visitLogRepository.findByVisitDate(LocalDate.now())
                .map(VisitLog::getVisitCount)
                .orElse(0L));
        
        LocalDate startOfWeek = LocalDate.now().minusDays(6);
        Long yeuCauTuanNay = Long.valueOf(yeuCauRepository.findByDateRangeNative(startOfWeek.atStartOfDay(), now).size());
        Long truyCapTuanNay = Long.valueOf(sumVisitsInRange(visitLogRepository.findAll(), startOfWeek, LocalDate.now()));
        
        stats.put("yeuCauHomNay", yeuCauHomNay);
        stats.put("truyCapHomNay", truyCapHomNay);
        stats.put("yeuCauTuanNay", yeuCauTuanNay);
        stats.put("truyCapTuanNay", truyCapTuanNay);
        stats.put("tongSanPham", Long.valueOf(sanPhamService.demTong()));
        
        return stats;
    }
}