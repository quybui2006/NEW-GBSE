package demo.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import demo.model.SanPham;
import demo.model.VisitLog;
import demo.model.YeuCau;
import demo.repository.SanPhamRepository;
import demo.repository.VisitLogRepository;
import demo.repository.YeuCauRepository;
import demo.service.FileUploadService;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class SanPhamController {

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private YeuCauRepository yeuCauRepository;

    @Autowired
    private VisitLogRepository visitLogRepository;

    @Autowired
    private FileUploadService fileUploadService;

    @GetMapping("/")
    public String trangChu() {
        return "index";
    }

    @GetMapping("/san-pham")
    public String danhSachSanPham(@RequestParam(required = false) String keyword, Model model) {
        List<SanPham> danhSach;
        if (keyword != null && !keyword.isEmpty()) {
            danhSach = sanPhamRepository.timKiem(keyword);
            model.addAttribute("keyword", keyword);
        } else {
            danhSach = sanPhamRepository.findAll();
        }
        model.addAttribute("danhSach", danhSach);
        return "san-pham";
    }

    @GetMapping("/lien-he")
    public String trangLienHe() {
        return "lien-he";
    }

    @GetMapping("/san-pham/{id}")
    public String chiTietSanPham(@PathVariable Long id, Model model) {
        SanPham sp = sanPhamRepository.findById(id).orElse(null);
        model.addAttribute("sp", sp);
        return "chi-tiet-san-pham";
    }

    @GetMapping("/login")
    public String trangLogin() {
        return "login";
    }

    @PostMapping("/lien-he")
    public String guiYeuCau(@ModelAttribute YeuCau yeuCau, Model model) {
        yeuCauRepository.save(yeuCau);
        model.addAttribute("thanhCong", true);
        return "lien-he";
    }

    @GetMapping("/admin")
    public String adminDashboard(@RequestParam(required = false) String from,
                                 @RequestParam(required = false) String to,
                                 Model model) {
        LocalDate fromDate;
        LocalDate toDate;

        try {
            fromDate = (from != null && !from.isEmpty()) ? LocalDate.parse(from) : LocalDate.now().minusDays(6);
            toDate = (to != null && !to.isEmpty()) ? LocalDate.parse(to) : LocalDate.now();
        } catch (Exception e) {
            fromDate = LocalDate.now().minusDays(6);
            toDate = LocalDate.now();
        }

        if (fromDate.isAfter(toDate)) {
            LocalDate temp = fromDate;
            fromDate = toDate;
            toDate = temp;
        }
        final LocalDate start = fromDate;
        final LocalDate end = toDate;

        List<YeuCau> allYeuCau = yeuCauRepository.findAll();
        List<VisitLog> allVisitLogs = visitLogRepository.findAll();

        LocalDateTime startDateTime = fromDate.atStartOfDay();
        LocalDateTime endDateTime = toDate.plusDays(1).atStartOfDay();

        long tongYeuCau = allYeuCau.stream()
            .filter(yc -> yc.getThoiGian() != null
                && !yc.getThoiGian().isBefore(startDateTime)
                && yc.getThoiGian().isBefore(endDateTime))
            .count();

        long tongTruyCap = allVisitLogs.stream()
            .filter(vl -> vl.getVisitDate() != null
                && !vl.getVisitDate().isBefore(start)
                && !vl.getVisitDate().isAfter(end))
            .mapToLong(VisitLog::getVisitCount)
            .sum();

        List<String> chartLabels = new ArrayList<>();
        List<Integer> chartAccessData = new ArrayList<>();
        List<Integer> chartRequestData = new ArrayList<>();

        LocalDate current = fromDate;
for (LocalDate d = fromDate; !d.isAfter(toDate); d = d.plusDays(1)) {
    final LocalDate currentDay = d;
    chartLabels.add(currentDay.format(DateTimeFormatter.ofPattern("dd/MM")));

    LocalDateTime dayStart = currentDay.atStartOfDay();
    LocalDateTime dayEnd = currentDay.plusDays(1).atStartOfDay();

    int reqCount = (int) allYeuCau.stream()
        .filter(yc -> yc.getThoiGian() != null
            && !yc.getThoiGian().isBefore(dayStart)
            && yc.getThoiGian().isBefore(dayEnd))
        .count();
    chartRequestData.add(reqCount);

    int visitCount = allVisitLogs.stream()
        .filter(vl -> vl.getVisitDate() != null && vl.getVisitDate().equals(currentDay))
        .mapToInt(VisitLog::getVisitCount)
        .sum();
    chartAccessData.add(visitCount);
}

        model.addAttribute("tongSanPham", sanPhamRepository.count());
        model.addAttribute("tongYeuCau", tongYeuCau);
        model.addAttribute("tongTruyCap", tongTruyCap);
        model.addAttribute("hasData", tongYeuCau > 0 || tongTruyCap > 0);
        model.addAttribute("chartLabels", chartLabels);
        model.addAttribute("chartAccessData", chartAccessData);
        model.addAttribute("chartRequestData", chartRequestData);
        model.addAttribute("fromDate", fromDate.toString());
        model.addAttribute("toDate", toDate.toString());
        return "admin/index";
    }

    @GetMapping("/admin/yeu-cau")
    public String danhSachYeuCau(Model model) {
        model.addAttribute("danhSachYeuCau", yeuCauRepository.findAll());
        return "admin/yeu-cau";
    }

    @GetMapping("/admin/san-pham")
    public String adminDanhSachSanPham(Model model) {
        model.addAttribute("danhSach", sanPhamRepository.findAll());
        return "admin/san-pham";
    }

    @GetMapping("/admin/san-pham/them")
    public String adminThemSanPham(Model model) {
        model.addAttribute("sp", new SanPham());
        return "admin/form-san-pham";
    }

    @GetMapping("/admin/san-pham/sua/{id}")
    public String adminSuaSanPham(@PathVariable Long id, Model model) {
        SanPham sp = sanPhamRepository.findById(id).orElse(null);
        model.addAttribute("sp", sp);
        return "admin/form-san-pham";
    }

    @PostMapping("/admin/san-pham/luu")
    public String adminLuuSanPham(@ModelAttribute SanPham sp,
            @RequestParam("hinhAnhFile") MultipartFile hinhAnhFile) {
        try {
            if (!hinhAnhFile.isEmpty()) {
                String tenFile = fileUploadService.luuFile(hinhAnhFile);
                sp.setHinhAnh(tenFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        sanPhamRepository.save(sp);
        return "redirect:/admin/san-pham";
    }

    @GetMapping("/admin/san-pham/xoa/{id}")
    public String adminXoaSanPham(@PathVariable Long id) {
        sanPhamRepository.deleteById(id);
        return "redirect:/admin/san-pham";
    }

    @GetMapping("/admin/bao-cao/excel")
    public void xuatExcel(@RequestParam(defaultValue = "all") String period,
                          HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=bao-cao-yeu-cau.xlsx");

        List<YeuCau> data = locTheoThoiGian(period);

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

    private List<YeuCau> locTheoThoiGian(String period) {
        LocalDateTime from = switch (period) {
            case "day" -> LocalDateTime.now().toLocalDate().atStartOfDay();
            case "week" -> LocalDateTime.now().minusWeeks(1);
            case "month" -> LocalDateTime.now().minusMonths(1);
            case "year" -> LocalDateTime.now().minusYears(1);
            default -> LocalDateTime.of(2000, 1, 1, 0, 0);
        };
        return yeuCauRepository.findAll().stream()
            .filter(yc -> yc.getThoiGian() != null && yc.getThoiGian().isAfter(from))
            .collect(Collectors.toList());
    }
}