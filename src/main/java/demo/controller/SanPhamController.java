package demo.controller;

import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import demo.model.Data;
import demo.model.SanPham;
import demo.model.YeuCau;
import demo.service.DashboardService;
import demo.service.SanPhamService;
import demo.service.YeuCauService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
public class SanPhamController {

    // ========== SERVICES ==========
    @Autowired
    private SanPhamService sanPhamService;

    @Autowired
    private YeuCauService yeuCauService;

    @Autowired
    private DashboardService dashboardService;

    // ================================================================
    //  TRANG CÔNG KHAI
    // ================================================================

    @GetMapping("/")
    public String trangChu(Model model) {
        model.addAttribute("customers", Data.CUSTOMERS);
        model.addAttribute("projects", Data.PROJECTS);
        return "home";
    }

    @GetMapping("/products")
    public String danhSachSanPham(@RequestParam(required = false) String keyword, Model model) {
        List<SanPham> danhSach = sanPhamService.timKiem(keyword);
        model.addAttribute("danhSach", danhSach);
        if (keyword != null && !keyword.isEmpty()) {
            model.addAttribute("keyword", keyword);
        }
        return "products";
    }

    @GetMapping("/contact")
    public String trangLienHe(Model model) {
        model.addAttribute("yeuCau", new YeuCau());
        return "contact";
    }

    @GetMapping("/product/{id}")
    public String chiTietSanPham(@PathVariable Long id, Model model) {
        SanPham sp = sanPhamService.timTheoId(id).orElse(null);
        if (sp == null) return "redirect:/products";
        model.addAttribute("product", sp);
        model.addAttribute("range", sp);
        return "product-detail";
    }

    @GetMapping("/login")
    public String trangLogin() {
        return "login";
    }

    @PostMapping("/contact")
    public String guiYeuCau(@Valid @ModelAttribute("yeuCau") YeuCau yeuCau,
                            BindingResult bindingResult,
                            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "contact";
        }
        yeuCauService.luuYeuCau(yeuCau);
        model.addAttribute("thanhCong", true);
        model.addAttribute("yeuCau", new YeuCau());
        return "contact";
    }

    // ================================================================
    //  TRANG ADMIN
    // ================================================================

    @GetMapping("/admin")
    public String adminDashboard(@RequestParam(required = false) String from,
                                 @RequestParam(required = false) String to,
                                 Model model) {
        Map<String, Object> data = dashboardService.getDashboardData(from, to);
        model.addAllAttributes(data);
        return "admin/index";
    }

    @GetMapping("/admin/yeu-cau")
    public String danhSachYeuCau(Model model) {
        model.addAttribute("danhSachYeuCau", yeuCauService.layTatCa());
        return "admin/yeu-cau";
    }

    @GetMapping("/admin/san-pham")
    public String adminDanhSachSanPham(Model model) {
        model.addAttribute("danhSach", sanPhamService.layTatCa());
        return "admin/san-pham";
    }

    @GetMapping("/admin/san-pham/them")
    public String adminThemSanPham(Model model) {
        model.addAttribute("sp", new SanPham());
        return "admin/form-san-pham";
    }

    @GetMapping("/admin/san-pham/sua/{id}")
    public String adminSuaSanPham(@PathVariable Long id, Model model) {
        SanPham sp = sanPhamService.timTheoId(id).orElse(null);
        model.addAttribute("sp", sp);
        return "admin/form-san-pham";
    }

    @PostMapping("/admin/san-pham/luu")
    public String adminLuuSanPham(@ModelAttribute SanPham sp,
                                  @RequestParam("hinhAnhFile") MultipartFile hinhAnhFile) {
        sanPhamService.luu(sp, hinhAnhFile);
        return "redirect:/admin/san-pham";
    }

    @PostMapping("/admin/san-pham/xoa/{id}")
    public String adminXoaSanPham(@PathVariable Long id) {
        sanPhamService.xoa(id);
        return "redirect:/admin/san-pham";
    }

    @PostMapping("/admin/bao-cao/excel")
    public void xuatExcel(@RequestParam(defaultValue = "all") String period,
                          @RequestParam(required = false) String from,
                          @RequestParam(required = false) String to,
                          HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=bao-cao-yeu-cau.xlsx");

        List<YeuCau> data = yeuCauService.locTheoThoiGian(period, from, to);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            var sheet = workbook.createSheet("Yeu Cau");
            var header = sheet.createRow(0);
            String[] cols = {"STT", "Họ tên", "SĐT", "Email", "Sản phẩm quan tâm", "Nội dung", "Thời gian"};
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
                row.createCell(4).setCellValue(yc.getSanPhamQuanTam());
                row.createCell(5).setCellValue(yc.getNoiDung());
                row.createCell(6).setCellValue(yc.getThoiGian() != null ? yc.getThoiGian().toString() : "");
            }
            workbook.write(response.getOutputStream());
        }
    }
}