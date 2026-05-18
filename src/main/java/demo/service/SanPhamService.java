package demo.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import demo.model.RangeGroup;
import demo.model.SanPham;
import demo.repository.SanPhamDropdown;
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

    /**
     * Returns all products, cached for 10 minutes.
     * Cache is evicted on any save/delete so the public site stays current.
     */
    @Cacheable("products")
    @Transactional(readOnly = true)
    public List<SanPham> layTatCa() {
        return sanPhamRepository.findAll(Sort.by("id").ascending());
    }

    /** Lightweight projection — only id + ten fetched. Use for dropdowns. */
    @Transactional(readOnly = true)
    public List<SanPhamDropdown> layDanhSachDropdown() {
        return sanPhamRepository.findAllProjectedBy(Sort.by("ten").ascending());
    }

    /** Groups a pre-fetched product list by rangeId in canonical order. */
    public List<RangeGroup> getRangeGroups(List<SanPham> all) {
        Map<String, List<SanPham>> byRange = new LinkedHashMap<>();
        for (SanPham sp : all) {
            String rId = sp.getRangeId();
            String key = (rId == null || rId.isBlank()) ? "__other__" : rId.trim().toLowerCase();
            byRange.computeIfAbsent(key, k -> new ArrayList<>()).add(sp);
        }

        List<RangeGroup> groups = new ArrayList<>();
        for (String rangeId : RANGE_ORDER) {
            List<SanPham> products = byRange.get(rangeId);
            if (products != null && !products.isEmpty()) {
                groups.add(new RangeGroup(
                    rangeId,
                    RANGE_BRANDS.getOrDefault(rangeId, rangeId.toUpperCase()),
                    RANGE_TAGLINES.getOrDefault(rangeId, ""),
                    products
                ));
            }
        }

        byRange.forEach((rangeId, products) -> {
            if (!RANGE_ORDER.contains(rangeId) && !"__other__".equals(rangeId) && !products.isEmpty()) {
                groups.add(new RangeGroup(rangeId, RANGE_BRANDS.getOrDefault(rangeId, rangeId.toUpperCase()), "", products));
            }
        });

        return groups;
    }

    // ── Admin CRUD ───────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Optional<SanPham> timTheoId(Long id) {
        return sanPhamRepository.findById(id);
    }

    @CacheEvict(value = "products", allEntries = true)
    @Transactional
    public SanPham luu(SanPham sanPham, MultipartFile hinhAnhFile) {
        validateJsonField(sanPham.getFeatures(), "Key Features");
        validateJsonField(sanPham.getApplications(), "Applications");
        validateJsonField(sanPham.getSpecs(), "Technical Specs");

        // Only Partners products may store external URLs.
        if (!"partners".equals(sanPham.getRangeId())) {
            sanPham.setLink(null);
        }

        if (hinhAnhFile != null && !hinhAnhFile.isEmpty()) {
            try {
                sanPham.setHinhAnh(fileUploadService.luuFile(hinhAnhFile));
            } catch (Exception e) {
                throw new RuntimeException("Cannot save image: " + e.getMessage(), e);
            }
        }
        return sanPhamRepository.save(sanPham);
    }

    @CacheEvict(value = "products", allEntries = true)
    @Transactional
    public void xoa(Long id) {
        sanPhamRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long demTong() {
        return sanPhamRepository.count();
    }

    @Transactional(readOnly = true)
    public List<SanPham> timKiem(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return sanPhamRepository.findAll();
        }
        return sanPhamRepository.timKiem(keyword.trim());
    }

    // ── Admin form text ↔ JSON converters ────────────────────────────────────

    public String jsonArrayToLines(String json) {
        if (json == null || json.isBlank()) return "";
        try {
            List<String> items = mapper.readValue(json, new TypeReference<List<String>>() {});
            return String.join("\n", items);
        } catch (Exception e) {
            return "";
        }
    }

    public String linesToJsonArray(String lines) {
        if (lines == null || lines.isBlank()) return null;
        List<String> items = Arrays.stream(lines.split("\n"))
            .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        if (items.isEmpty()) return null;
        try { return mapper.writeValueAsString(items); } catch (Exception e) { return null; }
    }

    public String jsonSpecsToLines(String json) {
        if (json == null || json.isBlank()) return "";
        try {
            List<SanPham.SpecEntry> specs = mapper.readValue(json, new TypeReference<List<SanPham.SpecEntry>>() {});
            return specs.stream().map(s -> s.label + ": " + s.value).collect(Collectors.joining("\n"));
        } catch (Exception e) {
            return "";
        }
    }

    public String linesToJsonSpecs(String lines) {
        if (lines == null || lines.isBlank()) return null;
        List<Map<String, String>> specs = Arrays.stream(lines.split("\n"))
            .map(String::trim).filter(s -> !s.isEmpty())
            .map(line -> {
                Map<String, String> entry = new LinkedHashMap<>();
                int idx = line.indexOf(": ");
                if (idx > 0) {
                    entry.put("label", line.substring(0, idx).trim());
                    entry.put("value", line.substring(idx + 2).trim());
                } else {
                    entry.put("label", line);
                    entry.put("value", "");
                }
                return entry;
            })
            .collect(Collectors.toList());
        if (specs.isEmpty()) return null;
        try { return mapper.writeValueAsString(specs); } catch (Exception e) { return null; }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void validateJsonField(String value, String fieldName) {
        if (value == null || value.isBlank()) return;
        try {
            JsonNode node = mapper.readTree(value);
            if (!node.isArray()) {
                throw new IllegalArgumentException(fieldName + ": must be a JSON array");
            }
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(fieldName + ": invalid JSON — " + e.getOriginalMessage());
        }
    }
}
