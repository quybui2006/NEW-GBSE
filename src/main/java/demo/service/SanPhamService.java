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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import demo.model.RangeGroup;
import demo.model.SanPham;
import demo.repository.SanPhamRepository;

@Service
public class SanPhamService {

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

    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private FileUploadService fileUploadService;

    // ── Public catalogue ─────────────────────────────────────────────────────

    /** Fetches all products from DB then delegates to the list overload. */
    public List<RangeGroup> getRangeGroups() {
        return getRangeGroups(sanPhamRepository.findAll(Sort.by("id").ascending()));
    }

    /**
     * Groups a pre-fetched product list by rangeId in canonical order.
     * Null or blank rangeId products are silently excluded (not rendered).
     */
    public List<RangeGroup> getRangeGroups(List<SanPham> all) {
        Map<String, List<SanPham>> byRange = new LinkedHashMap<>();
        for (SanPham sp : all) {
            String rId = sp.getRangeId();
            // Null or blank rangeId → bucket as __other__ (never rendered)
            String key = (rId == null || rId.isBlank()) ? "__other__" : rId.trim().toLowerCase();
            byRange.computeIfAbsent(key, k -> new ArrayList<>()).add(sp);
        }

        List<RangeGroup> groups = new ArrayList<>();

        // Emit known ranges in canonical order.
        for (String rangeId : RANGE_ORDER) {
            List<SanPham> products = byRange.get(rangeId);
            if (products != null && !products.isEmpty()) {
                String brand   = RANGE_BRANDS.getOrDefault(rangeId, rangeId.toUpperCase());
                String tagline = RANGE_TAGLINES.getOrDefault(rangeId, "");
                groups.add(new RangeGroup(rangeId, brand, tagline, products));
            }
        }

        // Append any unrecognised (but non-blank) rangeIds after the known ones.
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

    /**
     * Validates JSON array fields, then saves the product.
     * Throws IllegalArgumentException with a user-readable message on bad input.
     */
    public SanPham luu(SanPham sanPham, MultipartFile hinhAnhFile) {
        validateJsonField(sanPham.getFeatures(), "Key Features");
        validateJsonField(sanPham.getApplications(), "Applications");
        validateJsonField(sanPham.getSpecs(), "Technical Specs");

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

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void validateJsonField(String value, String fieldName) {
        if (value == null || value.isBlank()) return;
        try {
            JsonNode node = mapper.readTree(value);
            if (!node.isArray()) {
                throw new IllegalArgumentException(
                    fieldName + ": phải là JSON array, ví dụ [\"a\",\"b\"]");
            }
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                fieldName + ": JSON không hợp lệ — " + e.getOriginalMessage());
        }
    }
}
