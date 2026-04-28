package demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import demo.model.YeuCau;
import demo.repository.YeuCauRepository;

@Service
public class YeuCauService {

    @Autowired
    private YeuCauRepository yeuCauRepository;

    // Lưu yêu cầu mới
    public YeuCau luuYeuCau(YeuCau yeuCau) {
        yeuCau.setThoiGian(LocalDateTime.now());
        return yeuCauRepository.save(yeuCau);
    }

    // Lấy tất cả yêu cầu
    public List<YeuCau> layTatCa() {
        return yeuCauRepository.findAll();
    }

    // Lọc yêu cầu theo khoảng thời gian
    public List<YeuCau> locTheoThoiGian(String period, String from, String to) {
        LocalDateTime fromDateTime;

        if ("custom".equals(period) && from != null && !from.isEmpty()) {
            fromDateTime = LocalDate.parse(from).atStartOfDay();
        } else {
            fromDateTime = switch (period) {
                case "day" -> LocalDateTime.now().toLocalDate().atStartOfDay();
                case "week" -> LocalDateTime.now().minusWeeks(1);
                case "month" -> LocalDateTime.now().minusMonths(1);
                case "year" -> LocalDateTime.now().minusYears(1);
                default -> LocalDateTime.of(2000, 1, 1, 0, 0);
            };
        }

        LocalDateTime toDateTime = null;
        if ("custom".equals(period) && to != null && !to.isEmpty()) {
            toDateTime = LocalDate.parse(to).plusDays(1).atStartOfDay();
        }

        final LocalDateTime finalFrom = fromDateTime;
        final LocalDateTime finalTo = toDateTime;

        return yeuCauRepository.findAll().stream()
                .filter(yc -> yc.getThoiGian() != null && !yc.getThoiGian().isBefore(finalFrom))
                .filter(yc -> finalTo == null || yc.getThoiGian().isBefore(finalTo))
                .collect(Collectors.toList());
    }

    // Xóa yêu cầu
    public void xoa(Long id) {
        yeuCauRepository.deleteById(id);
    }
}