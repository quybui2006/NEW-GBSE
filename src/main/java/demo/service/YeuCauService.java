package demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import demo.dto.AdminYeuCauUpdateDto;
import demo.model.YeuCau;
import demo.repository.YeuCauRepository;

@Service
public class YeuCauService {

    @Autowired
    private YeuCauRepository yeuCauRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Persists the request then fires the async email notification.
     * @Transactional ensures the DB write either fully succeeds or rolls back.
     * The email fires after the transaction commits — if the email fails,
     * the persisted record is not rolled back (data is never lost).
     */
    @Transactional
    public YeuCau luuYeuCau(YeuCau yeuCau) {
        YeuCau saved = yeuCauRepository.save(yeuCau);
        emailService.guiThongBao(saved);
        return saved;
    }

    @Transactional
    public void updateContactStatus(Long id, AdminYeuCauUpdateDto dto) {

        YeuCau yeuCau = yeuCauRepository.findById(id)
            .orElseThrow(() ->
                    new IllegalArgumentException("YeuCau not found: " + id));

    yeuCau.setContacted(dto.isContacted());
    yeuCau.setAdminNote(dto.getAdminNote());
    }

    @Transactional(readOnly = true)
    public List<YeuCau> layTatCa() {
        return yeuCauRepository.findAll(Sort.by("thoiGian").descending());
    }

    @Transactional(readOnly = true)
    public List<YeuCau> locTheoThoiGian(String period, String from, String to) {
        LocalDateTime fromDateTime;
        LocalDateTime toDateTime = null;

        if ("custom".equals(period) && from != null && !from.isEmpty()) {
            fromDateTime = LocalDate.parse(from).atStartOfDay();
            if (to != null && !to.isEmpty()) {
                toDateTime = LocalDate.parse(to).plusDays(1).atStartOfDay();
            }
        } else {
            fromDateTime = switch (period) {
                case "day"   -> LocalDateTime.now().toLocalDate().atStartOfDay();
                case "week"  -> LocalDateTime.now().minusWeeks(1);
                case "month" -> LocalDateTime.now().minusMonths(1);
                case "year"  -> LocalDateTime.now().minusYears(1);
                default      -> LocalDateTime.of(2000, 1, 1, 0, 0);
            };
        }

        return (toDateTime != null)
                ? yeuCauRepository.findByDateRange(fromDateTime, toDateTime)
                : yeuCauRepository.findByDateRangeFrom(fromDateTime);
    }

    /**
     * Returns page {@code page} (0-based, 20 rows, newest first).
     * Invalid date strings silently fall back to an unfiltered query.
     */
    @Transactional(readOnly = true)
    public Page<YeuCau> layTrangCoLoc(String from, String to, int page) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by("thoiGian").descending());
        if (from != null && !from.isEmpty()) {
            try {
                LocalDateTime fromDt = LocalDate.parse(from).atStartOfDay();
                LocalDateTime toDt = (to != null && !to.isEmpty())
                        ? LocalDate.parse(to).plusDays(1).atStartOfDay()
                        : LocalDate.now().plusDays(1).atStartOfDay();
                return yeuCauRepository.findByDateRangePaged(fromDt, toDt, pageable);
            } catch (Exception ignored) {
                // bad date format — fall through to unfiltered
            }
        }
        return yeuCauRepository.findAll(pageable);
    }

    @Transactional
    public void xoa(Long id) {
        yeuCauRepository.deleteById(id);
    }
}
