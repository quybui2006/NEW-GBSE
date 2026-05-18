package demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import demo.model.SanPham;
import demo.service.SanPhamService;

@Controller
@RequestMapping("/admin/san-pham")
public class AdminProductController {

    private final SanPhamService sanPhamService;

    @Autowired
    public AdminProductController(SanPhamService sanPhamService) {
        this.sanPhamService = sanPhamService;
    }

    // Treat blank price field as null rather than a binding error
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Double.class, new CustomNumberEditor(Double.class, true));
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("danhSach", sanPhamService.layTatCa());
        return "admin/san-pham";
    }

    @GetMapping("/them")
    public String addForm(Model model) {
        model.addAttribute("sp", new SanPham());
        model.addAttribute("featuresLines", "");
        model.addAttribute("applicationsLines", "");
        model.addAttribute("specsLines", "");
        return "admin/form-san-pham";
    }

    @GetMapping("/sua/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        SanPham sp = sanPhamService.timTheoId(id).orElse(null);
        if (sp == null) return "redirect:/admin/san-pham";
        model.addAttribute("sp", sp);
        model.addAttribute("featuresLines",     sanPhamService.jsonArrayToLines(sp.getFeatures()));
        model.addAttribute("applicationsLines", sanPhamService.jsonArrayToLines(sp.getApplications()));
        model.addAttribute("specsLines",        sanPhamService.jsonSpecsToLines(sp.getSpecs()));
        return "admin/form-san-pham";
    }

    @PostMapping("/luu")
    public String save(@ModelAttribute SanPham sp,
                       @RequestParam("hinhAnhFile") MultipartFile hinhAnhFile,
                       @RequestParam(name = "featuresRaw",     required = false) String featuresRaw,
                       @RequestParam(name = "applicationsRaw", required = false) String applicationsRaw,
                       @RequestParam(name = "specsRaw",        required = false) String specsRaw,
                       RedirectAttributes redirectAttributes) {
        sp.setFeatures(sanPhamService.linesToJsonArray(featuresRaw));
        sp.setApplications(sanPhamService.linesToJsonArray(applicationsRaw));
        sp.setSpecs(sanPhamService.linesToJsonSpecs(specsRaw));
        try {
            sanPhamService.luu(sp, hinhAnhFile);
            return "redirect:/admin/san-pham";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("saveError", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("saveError", "Save failed: " + e.getMessage());
        }
        return sp.getId() == null
                ? "redirect:/admin/san-pham/them"
                : "redirect:/admin/san-pham/sua/" + sp.getId();
    }

    @PostMapping("/xoa/{id}")
    public String delete(@PathVariable Long id) {
        sanPhamService.xoa(id);
        return "redirect:/admin/san-pham";
    }
}
