package demo.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import demo.model.RangeGroup;
import demo.model.SanPham;
import demo.repository.SanPhamRepository;

@Service
public class SanPhamService {

    // Canonical display order for the three product ranges.
    private static final List<String> RANGE_ORDER = List.of("omeax", "gbse", "partners");

    private static final Map<String, String> RANGE_BRANDS = Map.of(
        "omeax",    "OMEAX",
        "gbse",     "GBSE",
        "partners", "Partners"
    );

    private static final Map<String, String> RANGE_TAGLINES = Map.of(
        "omeax",    "Bơm & Van công nghiệp",
        "gbse",     "Van cân bằng · Heatpump · Trao đổi nhiệt · Đo lường",
        "partners", "Hệ thống ứng dụng — liên kết hãng"
    );

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private FileUploadService fileUploadService;

    // ── Public catalogue ─────────────────────────────────────────────────────

    /**
     * Returns all DB products grouped by rangeId in the canonical order.
     * Products without a rangeId are appended in a catch-all group at the end.
     */
    public List<RangeGroup> getRangeGroups() {
        List<SanPham> all = sanPhamRepository.findAll(Sort.by("id").ascending());

        // Preserve insertion order so RANGE_ORDER controls display sequence.
        Map<String, List<SanPham>> byRange = new LinkedHashMap<>();
        for (SanPham sp : all) {
            String key = sp.getRangeId() != null ? sp.getRangeId() : "__other__";
            byRange.computeIfAbsent(key, k -> new ArrayList<>()).add(sp);
        }

        List<RangeGroup> groups = new ArrayList<>();

        // Emit groups in canonical order first.
        for (String rangeId : RANGE_ORDER) {
            List<SanPham> products = byRange.get(rangeId);
            if (products != null && !products.isEmpty()) {
                String brand   = RANGE_BRANDS.getOrDefault(rangeId, rangeId.toUpperCase());
                String tagline = RANGE_TAGLINES.getOrDefault(rangeId, "");
                groups.add(new RangeGroup(rangeId, brand, tagline, products));
            }
        }

        // Append any unrecognised rangeIds at the end.
        byRange.forEach((rangeId, products) -> {
            if (!RANGE_ORDER.contains(rangeId) && !"__other__".equals(rangeId) && !products.isEmpty()) {
                String brand = RANGE_BRANDS.getOrDefault(rangeId, rangeId.toUpperCase());
                groups.add(new RangeGroup(rangeId, brand, "", products));
            }
        });

        return groups;
    }

    // ── Admin CRUD ───────────────────────────────────────────────────────────

    public List<SanPham> layTatCa() {
        return sanPhamRepository.findAll(Sort.by("id").ascending());
    }

    public Optional<SanPham> timTheoId(Long id) {
        return sanPhamRepository.findById(id);
    }

    public SanPham luu(SanPham sanPham, MultipartFile hinhAnhFile) {
        if (hinhAnhFile != null && !hinhAnhFile.isEmpty()) {
            try {
                String tenFile = fileUploadService.luuFile(hinhAnhFile);
                sanPham.setHinhAnh(tenFile);
            } catch (Exception e) {
                throw new RuntimeException("Không thể lưu ảnh: " + e.getMessage(), e);
            }
        }
        return sanPhamRepository.save(sanPham);
    }

    public void xoa(Long id) {
        sanPhamRepository.deleteById(id);
    }

    public long demTong() {
        return sanPhamRepository.count();
    }

    public List<SanPham> timKiem(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return sanPhamRepository.findAll();
        }
        return sanPhamRepository.timKiem(keyword.trim());
    }
}
