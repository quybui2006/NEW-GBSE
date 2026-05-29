package demo.config;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import demo.model.RangeGroup;
import demo.service.SanPhamService;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalModelAttributes {

    private final SanPhamService sanPhamService;

    @Autowired
    public GlobalModelAttributes(SanPhamService sanPhamService) {
        this.sanPhamService = sanPhamService;
    }

    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("navRanges")
    public List<RangeGroup> navRanges() {
        try {
            return sanPhamService.getRangeGroups(sanPhamService.layTatCa());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
