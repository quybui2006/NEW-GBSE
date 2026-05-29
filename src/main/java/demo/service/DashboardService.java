package demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import demo.repository.ContactRequestRepository;

@Service
public class DashboardService {

    private final ContactRequestRepository contactRequestRepository;

    public DashboardService(ContactRequestRepository contactRequestRepository) {
        this.contactRequestRepository = contactRequestRepository;
    }

    /**
     * Builds dashboard stats for the given date range.
     * Chart data is aggregated in the database with GROUP BY DATE — only one
     * lightweight row per day is transferred, not all ContactRequest entities.
     */
    public Map<String, Object> getDashboardData(String from, String to) {
        LocalDate fromDate = parseDate(from, LocalDate.now().minusDays(6));
        LocalDate toDate   = parseDate(to,   LocalDate.now());

        if (fromDate.isAfter(toDate)) {
            LocalDate temp = fromDate; fromDate = toDate; toDate = temp;
        }

        LocalDateTime startDateTime = fromDate.atStartOfDay();
        LocalDateTime endDateTime   = toDate.plusDays(1).atStartOfDay();

        // Aggregate in the DB — one row per day, not full entity load
        List<Object[]> dayCounts = contactRequestRepository.countByDay(startDateTime, endDateTime);

        // Build a label→count lookup from the DB result
        Map<String, Long> countByLabel = new HashMap<>();
        long totalRequests = 0;
        for (Object[] row : dayCounts) {
            // row[0] = java.sql.Date, row[1] = Long
            String label = row[0].toString().substring(5).replace("-", "/"); // MM/DD → dd/MM
            long count = ((Number) row[1]).longValue();
            // Reformat to dd/MM to match the chart label format
            String[] parts = label.split("/");
            String ddMM = parts[1] + "/" + parts[0];
            countByLabel.put(ddMM, count);
            totalRequests += count;
        }

        DateTimeFormatter labelFmt = DateTimeFormatter.ofPattern("dd/MM");
        List<String> labels      = new ArrayList<>();
        List<Long>   requestData = new ArrayList<>();

        for (LocalDate d = fromDate; !d.isAfter(toDate); d = d.plusDays(1)) {
            String label = d.format(labelFmt);
            labels.add(label);
            requestData.add(countByLabel.getOrDefault(label, 0L));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalRequests",    totalRequests);
        result.put("hasData",          totalRequests > 0);
        result.put("chartLabels",      labels);
        result.put("chartRequestData", requestData);
        result.put("fromDate",         fromDate.toString());
        result.put("toDate",           toDate.toString());
        return result;
    }

    private LocalDate parseDate(String dateStr, LocalDate defaultDate) {
        try {
            return (dateStr != null && !dateStr.isEmpty()) ? LocalDate.parse(dateStr) : defaultDate;
        } catch (Exception e) {
            return defaultDate;
        }
    }
}
