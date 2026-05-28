package demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import demo.model.YeuCau;
import demo.service.DashboardService;
import demo.service.YeuCauService;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final DashboardService dashboardService;
    private final YeuCauService yeuCauService;

    @Autowired
    public AdminDashboardController(DashboardService dashboardService, YeuCauService yeuCauService) {
        this.dashboardService = dashboardService;
        this.yeuCauService = yeuCauService;
    }

    @GetMapping
    public String dashboard(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(required = false) String from,
                            @RequestParam(required = false) String to,
                            Model model) {
        // Chart always shows last 7 days
        Map<String, Object> chartData = dashboardService.getDashboardData(null, null);
        model.addAllAttributes(chartData);

        // Request table with optional date filter and pagination
        Page<YeuCau> yeuCauPage = yeuCauService.layTrangCoLoc(from, to, page);
        model.addAttribute("danhSachYeuCau", yeuCauPage.getContent());
        model.addAttribute("currentPage",    yeuCauPage.getNumber());
        model.addAttribute("totalPages",     yeuCauPage.getTotalPages());
        model.addAttribute("totalItems",     yeuCauPage.getTotalElements());
        model.addAttribute("from", from);
        model.addAttribute("to",   to);

        return "admin/index";
    }
}
