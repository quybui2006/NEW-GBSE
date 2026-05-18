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

import demo.model.YeuCau;
import demo.repository.YeuCauRepository;

@Service
public class DashboardService {

    @Autowired
    private YeuCauRepository yeuCauRepository;

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

        List<YeuCau> filteredYeuCau = yeuCauRepository.findByDateRangeNative(startDateTime, endDateTime);

        List<String> labels      = new ArrayList<>();
        List<Long>   requestData = new ArrayList<>();

        for (LocalDate d = fromDate; !d.isAfter(toDate); d = d.plusDays(1)) {
            labels.add(d.format(DateTimeFormatter.ofPattern("dd/MM")));
            requestData.add(countRequestsOnDate(filteredYeuCau, d));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("tongSanPham",      (long) sanPhamService.demTong());
        result.put("tongYeuCau",       (long) filteredYeuCau.size());
        result.put("hasData",          !filteredYeuCau.isEmpty());
        result.put("chartLabels",      labels);
        result.put("chartRequestData", requestData);
        result.put("fromDate",         fromDate.toString());
        result.put("toDate",           toDate.toString());
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

        var pageable   = org.springframework.data.domain.PageRequest.of(page, size);
        var yeuCauPage = yeuCauRepository.findByDateRangeCoPhanTrang(startDateTime, endDateTime, pageable);

        List<String> labels      = new ArrayList<>();
        List<Long>   requestData = new ArrayList<>();

        for (LocalDate d = fromDate; !d.isAfter(toDate); d = d.plusDays(1)) {
            labels.add(d.format(DateTimeFormatter.ofPattern("dd/MM")));
            requestData.add(countRequestsOnDate(yeuCauPage.getContent(), d));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("tongSanPham",      (long) sanPhamService.demTong());
        result.put("tongYeuCau",       yeuCauPage.getTotalElements());
        result.put("hasData",          yeuCauPage.getTotalElements() > 0);
        result.put("chartLabels",      labels);
        result.put("chartRequestData", requestData);
        result.put("fromDate",         fromDate.toString());
        result.put("toDate",           toDate.toString());
        result.put("yeuCauList",       yeuCauPage.getContent());
        result.put("currentPage",      yeuCauPage.getNumber());
        result.put("totalPages",       yeuCauPage.getTotalPages());
        result.put("totalItems",       yeuCauPage.getTotalElements());
        return result;
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
}
