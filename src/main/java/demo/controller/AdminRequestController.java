package demo.controller;

import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import demo.dto.ContactUpdateDto;
import demo.model.ContactRequest;
import demo.service.ContactRequestService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminRequestController {

    private final ContactRequestService contactRequestService;

    public AdminRequestController(ContactRequestService contactRequestService) {
        this.contactRequestService = contactRequestService;
    }

    @PostMapping("/requests/{id}/update")
    public String update(
            @PathVariable Long id,
            @ModelAttribute @Valid ContactUpdateDto dto,
            BindingResult bindingResult,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        if (!bindingResult.hasErrors()) {
            contactRequestService.updateContactStatus(id, dto);
        }
        StringBuilder url = new StringBuilder("redirect:/admin?page=").append(page);
        if (from != null && !from.isBlank()) url.append("&from=").append(from);
        if (to   != null && !to.isBlank())   url.append("&to=").append(to);
        return url.toString();
    }

    @PostMapping("/reports/excel")
    public void exportExcel(@RequestParam(defaultValue = "all") String period,
                            @RequestParam(required = false) String from,
                            @RequestParam(required = false) String to,
                            HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=contact-requests.xlsx");

        List<ContactRequest> data = contactRequestService.filterByPeriod(period, from, to);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            var sheet  = workbook.createSheet("Requests");
            var header = sheet.createRow(0);
            String[] cols = {"STT", "Họ tên", "SĐT", "Email", "Nội dung", "Thời gian", "Trạng thái", "Ghi chú admin"};
            for (int i = 0; i < cols.length; i++) {
                header.createCell(i).setCellValue(cols[i]);
            }
            for (int i = 0; i < data.size(); i++) {
                var row = sheet.createRow(i + 1);
                ContactRequest request = data.get(i);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(request.getFullName());
                row.createCell(2).setCellValue(request.getPhone());
                row.createCell(3).setCellValue(request.getEmail());
                row.createCell(4).setCellValue(request.getMessage());
                row.createCell(5).setCellValue(request.getCreatedAt() != null ? request.getCreatedAt().toString() : "");
                row.createCell(6).setCellValue(request.isContacted() ? "Contacted" : "Not Contacted");
                row.createCell(7).setCellValue(request.getAdminNote() != null ? request.getAdminNote() : "");
            }
            workbook.write(response.getOutputStream());
        }
    }
}
