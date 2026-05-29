package demo.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import demo.model.ContactRequest;
import demo.service.ContactRequestService;
import demo.service.DashboardService;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final DashboardService dashboardService;
    private final ContactRequestService contactRequestService;

    public AdminDashboardController(DashboardService dashboardService,
                                    ContactRequestService contactRequestService) {
        this.dashboardService = dashboardService;
        this.contactRequestService = contactRequestService;
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
        Page<ContactRequest> requestPage = contactRequestService.findPage(from, to, page);
        model.addAttribute("requests",    requestPage.getContent());
        model.addAttribute("currentPage", requestPage.getNumber());
        model.addAttribute("totalPages",  requestPage.getTotalPages());
        model.addAttribute("totalItems",  requestPage.getTotalElements());
        model.addAttribute("from", from);
        model.addAttribute("to",   to);

        return "admin/index";
    }
}
