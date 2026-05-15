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
        LocalDate toDate   = parseDate(to,   LocalDate.now());

        if (fromDate.isAfter(toDate)) {
            LocalDate temp = fromDate;
            fromDate = toDate;
            toDate = temp;
        }

        LocalDateTime startDateTime = fromDate.atStartOfDay();
        LocalDateTime endDateTime   = toDate.plusDays(1).atStartOfDay();

        List<YeuCau>   filteredYeuCau = yeuCauRepository.findByDateRangeNative(startDateTime, endDateTime);
        // Fetch only the rows in the requested window — not the entire table.
        List<VisitLog> visitLogs      = visitLogRepository.findByVisitDateBetween(fromDate, toDate);

        Long tongYeuCau  = (long) filteredYeuCau.size();
        long totalVisits = sumVisits(visitLogs);
        boolean hasData  = !filteredYeuCau.isEmpty() || totalVisits > 0;

        List<String> labels      = new ArrayList<>();
        List<Long>   accessData  = new ArrayList<>();
        List<Long>   requestData = new ArrayList<>();

        for (LocalDate d = fromDate; !d.isAfter(toDate); d = d.plusDays(1)) {
            labels.add(d.format(DateTimeFormatter.ofPattern("dd/MM")));
            requestData.add(countRequestsOnDate(filteredYeuCau, d));
            accessData.add(visitsOnDate(visitLogs, d));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("tongSanPham",    (long) sanPhamService.demTong());
        result.put("tongYeuCau",     tongYeuCau);
        result.put("tongTruyCap",    totalVisits);
        result.put("hasData",        hasData);
        result.put("chartLabels",    labels);
        result.put("chartAccessData",  accessData);
        result.put("chartRequestData", requestData);
        result.put("fromDate", fromDate.toString());
        result.put("toDate",   toDate.toString());
        return result;
    }

    public Map<String, Object> getDashboardDataWithPagination(String from, String to, int page, int size) {
        LocalDate fromDate = parseDate(from, LocalDate.now().minusDays(6));
        LocalDate toDate   = parseDate(to,   LocalDate.now());

        if (fromDate.isAfter(toDate)) {
            LocalDate temp = fromDate;
            fromDate = toDate;
            toDate = temp;
        }

        LocalDateTime startDateTime = fromDate.atStartOfDay();
        LocalDateTime endDateTime   = toDate.plusDays(1).atStartOfDay();

        var pageable    = org.springframework.data.domain.PageRequest.of(page, size);
        var yeuCauPage  = yeuCauRepository.findByDateRangeCoPhanTrang(startDateTime, endDateTime, pageable);
        List<VisitLog> visitLogs = visitLogRepository.findByVisitDateBetween(fromDate, toDate);

        long totalVisits = sumVisits(visitLogs);
        boolean hasData  = yeuCauPage.getTotalElements() > 0 || totalVisits > 0;

        List<String> labels      = new ArrayList<>();
        List<Long>   accessData  = new ArrayList<>();
        List<Long>   requestData = new ArrayList<>();

        for (LocalDate d = fromDate; !d.isAfter(toDate); d = d.plusDays(1)) {
            labels.add(d.format(DateTimeFormatter.ofPattern("dd/MM")));
            requestData.add(countRequestsOnDate(yeuCauPage.getContent(), d));
            accessData.add(visitsOnDate(visitLogs, d));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("tongSanPham",    (long) sanPhamService.demTong());
        result.put("tongYeuCau",     yeuCauPage.getTotalElements());
        result.put("tongTruyCap",    totalVisits);
        result.put("hasData",        hasData);
        result.put("chartLabels",    labels);
        result.put("chartAccessData",  accessData);
        result.put("chartRequestData", requestData);
        result.put("fromDate",   fromDate.toString());
        result.put("toDate",     toDate.toString());
        result.put("yeuCauList", yeuCauPage.getContent());
        result.put("currentPage",  yeuCauPage.getNumber());
        result.put("totalPages",   yeuCauPage.getTotalPages());
        result.put("totalItems",   yeuCauPage.getTotalElements());
        return result;
    }

    public Map<String, Object> getQuickStats() {
        LocalDate today     = LocalDate.now();
        LocalDate weekStart = today.minusDays(6);

        LocalDateTime now        = LocalDateTime.now();
        LocalDateTime startOfDay = today.atStartOfDay();

        List<VisitLog> todayLogs = visitLogRepository.findByVisitDateBetween(today, today);
        List<VisitLog> weekLogs  = visitLogRepository.findByVisitDateBetween(weekStart, today);

        Map<String, Object> stats = new HashMap<>();
        stats.put("yeuCauHomNay",    (long) yeuCauRepository.findByDateRangeNative(startOfDay, now).size());
        stats.put("truyCapHomNay",   sumVisits(todayLogs));
        stats.put("yeuCauTuanNay",   (long) yeuCauRepository.findByDateRangeNative(weekStart.atStartOfDay(), now).size());
        stats.put("truyCapTuanNay",  sumVisits(weekLogs));
        stats.put("tongSanPham",     (long) sanPhamService.demTong());
        return stats;
    }

    // ── private helpers ──────────────────────────────────────────────────────

    private LocalDate parseDate(String dateStr, LocalDate defaultDate) {
        try {
            return (dateStr != null && !dateStr.isEmpty()) ? LocalDate.parse(dateStr) : defaultDate;
        } catch (Exception e) {
            return defaultDate;
        }
    }

    private long countRequestsOnDate(List<YeuCau> list, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end   = date.plusDays(1).atStartOfDay();
        return list.stream()
                .filter(yc -> yc.getThoiGian() != null
                        && !yc.getThoiGian().isBefore(start)
                        &&  yc.getThoiGian().isBefore(end))
                .count();
    }

    private long visitsOnDate(List<VisitLog> list, LocalDate date) {
        return list.stream()
                .filter(vl -> date.equals(vl.getVisitDate()))
                .mapToLong(VisitLog::getVisitCount)
                .sum();
    }

    private long sumVisits(List<VisitLog> list) {
        return list.stream().mapToLong(VisitLog::getVisitCount).sum();
    }
}
