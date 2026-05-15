package demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import demo.model.YeuCau;
import demo.repository.YeuCauRepository;

@Service
public class YeuCauService {

    @Autowired
    private YeuCauRepository yeuCauRepository;

    @Autowired
    private EmailService emailService;

    public YeuCau luuYeuCau(YeuCau yeuCau) {
        YeuCau saved = yeuCauRepository.save(yeuCau);
        emailService.guiThongBao(saved);
        return saved;
    }

    public List<YeuCau> layTatCa() {
        return yeuCauRepository.findAll(Sort.by("thoiGian").descending());
    }

    public Page<YeuCau> layTatCaCoPhanTrang(int page, int size) {
        return yeuCauRepository.findAll(PageRequest.of(page, size, Sort.by("thoiGian").descending()));
    }

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

        // When toDateTime is null the JPQL "< :to" comparison returns 0 rows;
        // use the open-ended query instead.
        if (toDateTime != null) {
            return yeuCauRepository.findByDateRange(fromDateTime, toDateTime);
        } else {
            return yeuCauRepository.findByDateRangeFrom(fromDateTime);
        }
    }

    public void xoa(Long id) {
        yeuCauRepository.deleteById(id);
    }
}