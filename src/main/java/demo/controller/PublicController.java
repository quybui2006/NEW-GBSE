package demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import demo.dto.YeuCauFormDto;
import demo.model.SanPham;
import demo.model.YeuCau;
import demo.service.ContactRateLimiter;
import demo.service.SanPhamService;
import demo.service.YeuCauService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class PublicController {

    private final SanPhamService sanPhamService;
    private final YeuCauService yeuCauService;
    private final ContactRateLimiter contactRateLimiter;

    @Autowired
    public PublicController(SanPhamService sanPhamService,
                            YeuCauService yeuCauService,
                            ContactRateLimiter contactRateLimiter) {
        this.sanPhamService = sanPhamService;
        this.yeuCauService = yeuCauService;
        this.contactRateLimiter = contactRateLimiter;
    }

    @GetMapping("/")
    public String home() { return "home"; }

    @GetMapping("/about")
    public String about() { return "about"; }

    @GetMapping("/projects")
    public String projects() { return "projects"; }

    @GetMapping("/news")
    public String news() { return "news"; }

    @GetMapping("/products")
    public String products(Model model) {
        List<SanPham> all = sanPhamService.layTatCa();
        model.addAttribute("ranges", sanPhamService.getRangeGroups(all));
        model.addAttribute("allProducts", all);
        return "products";
    }

    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        SanPham sp = sanPhamService.timTheoId(id).orElse(null);
        if (sp == null) return "redirect:/products";
        model.addAttribute("product", sp);
        return "product-detail";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("contactForm", new YeuCauFormDto());
        // Lightweight projection — only id + name fetched for the checkbox list
        model.addAttribute("productDropdown", sanPhamService.layDanhSachDropdown());
        return "contact";
    }

    @PostMapping("/contact")
    public String submitContact(@Valid @ModelAttribute("contactForm") YeuCauFormDto form,
                                BindingResult bindingResult,
                                @RequestParam(name = "selectedProducts", required = false) List<String> selectedProducts,
                                @RequestParam(name = "website", required = false) String honeypot,
                                HttpServletRequest request,
                                Model model) {
        // Honeypot: the hidden field is empty for humans, filled by bots.
        // Silently discard — never tell bots why they failed.
        if (honeypot != null && !honeypot.isBlank()) {
            model.addAttribute("thanhCong", true);
            model.addAttribute("contactForm", new YeuCauFormDto());
            model.addAttribute("productDropdown", sanPhamService.layDanhSachDropdown());
            return "contact";
        }

        if (!contactRateLimiter.isAllowed(request.getRemoteAddr())) {
            model.addAttribute("rateLimitError", true);
            model.addAttribute("productDropdown", sanPhamService.layDanhSachDropdown());
            return "contact";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("productDropdown", sanPhamService.layDanhSachDropdown());
            return "contact";
        }

        // Map DTO → entity (only the four allowed fields are copied)
        YeuCau yeuCau = new YeuCau();
        yeuCau.setHoTen(form.getHoTen());
        yeuCau.setSoDienThoai(form.getSoDienThoai());
        yeuCau.setEmail(form.getEmail());
        yeuCau.setNoiDung(form.getNoiDung());

        if (selectedProducts != null && !selectedProducts.isEmpty()) {
            yeuCau.setSanPhamQuanTam(String.join(", ", selectedProducts));
        }

        yeuCauService.luuYeuCau(yeuCau);

        model.addAttribute("thanhCong", true);
        model.addAttribute("contactForm", new YeuCauFormDto());
        model.addAttribute("productDropdown", sanPhamService.layDanhSachDropdown());
        return "contact";
    }

    @GetMapping("/login")
    public String login() { return "login"; }
}
