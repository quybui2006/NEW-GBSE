package demo.config;

import java.util.Collections;
import java.util.List;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import demo.model.RangeGroup;
import demo.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalModelAttributes {

    private final ProductService productService;

    public GlobalModelAttributes(ProductService productService) {
        this.productService = productService;
    }

    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("canonicalUrl")
    public String canonicalUrl(HttpServletRequest request) {
        return "https://www.omeax.vn" + request.getRequestURI();
    }

    @ModelAttribute("ogDescription")
    public String ogDescription() {
        return "OMEAX – Your partner for mechanical and electrical equipment.";
    }

    @ModelAttribute("navRanges")
    public List<RangeGroup> navRanges() {
        try {
            return productService.getRangeGroups(productService.findAll());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
