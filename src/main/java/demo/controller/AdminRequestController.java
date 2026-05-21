package demo.controller;

import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import demo.dto.AdminYeuCauUpdateDto;
import demo.model.YeuCau;
import demo.service.YeuCauService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminRequestController {

    private final YeuCauService yeuCauService;

    @Autowired
    public AdminRequestController(YeuCauService yeuCauService) {
        this.yeuCauService = yeuCauService;
    }
    @PostMapping("/yeu-cau/{id}/cap-nhat")
    public String update(
            @PathVariable Long id,
            @ModelAttribute @Valid AdminYeuCauUpdateDto dto,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/admin/yeu-cau";
        }
        yeuCauService.updateContactStatus(id, dto);
        return "redirect:/admin/yeu-cau";
    }

    @GetMapping("/yeu-cau")
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) String from,
                       @RequestParam(required = false) String to,
                       Model model) {
        Page<YeuCau> yeuCauPage = yeuCauService.layTrangCoLoc(from, to, page);
        model.addAttribute("danhSachYeuCau", yeuCauPage.getContent());
        model.addAttribute("currentPage",    yeuCauPage.getNumber());
        model.addAttribute("totalPages",     yeuCauPage.getTotalPages());
        model.addAttribute("totalItems",     yeuCauPage.getTotalElements());
        model.addAttribute("from", from);
        model.addAttribute("to",   to);
        return "admin/yeu-cau";
    }
    
    
    @PostMapping("/bao-cao/excel")
    public void exportExcel(@RequestParam(defaultValue = "all") String period,
                            @RequestParam(required = false) String from,
                            @RequestParam(required = false) String to,
                            HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=bao-cao-yeu-cau.xlsx");

        List<YeuCau> data = yeuCauService.locTheoThoiGian(period, from, to);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            var sheet  = workbook.createSheet("Yeu Cau");
            var header = sheet.createRow(0);
            String[] cols = {"STT", "Họ tên", "SĐT", "Email", "Nội dung", "Thời gian"};
            for (int i = 0; i < cols.length; i++) {
                header.createCell(i).setCellValue(cols[i]);
            }
            for (int i = 0; i < data.size(); i++) {
                var row = sheet.createRow(i + 1);
                YeuCau yc = data.get(i);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(yc.getHoTen());
                row.createCell(2).setCellValue(yc.getSoDienThoai());
                row.createCell(3).setCellValue(yc.getEmail());
                row.createCell(4).setCellValue(yc.getNoiDung());
                row.createCell(5).setCellValue(yc.getThoiGian() != null ? yc.getThoiGian().toString() : "");
            }
            workbook.write(response.getOutputStream());
        }
    }
}
