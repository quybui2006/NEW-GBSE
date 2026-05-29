package demo.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import demo.model.Product;
import demo.model.RangeGroup;
import demo.repository.ProductRepository;

@Service
public class ProductService {

    private static final List<String> RANGE_ORDER = List.of("grundfos", "oceancooling", "partners");

    private static final Map<String, String> RANGE_BRANDS = Map.of(
        "grundfos",     "Grundfos Pumps",
        "oceancooling", "Ocean Cooling Tower",
        "partners",     "Partners"
    );

    private static final Map<String, String> RANGE_TAGLINES = Map.of(
        "grundfos",     "Bơm & Van công nghiệp",
        "oceancooling", "Van cân bằng · Heatpump · Trao đổi nhiệt · Đo lường",
        "partners",     "Hệ thống ứng dụng — liên kết hãng"
    );

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // ── Public catalogue ─────────────────────────────────────────────────────

    @Cacheable("products")
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll(Sort.by("id").ascending());
    }

    /** Groups a pre-fetched product list by rangeId in canonical order. */
    public List<RangeGroup> getRangeGroups(List<Product> all) {
        Map<String, List<Product>> byRange = new LinkedHashMap<>();
        for (Product product : all) {
            String rId = product.getRangeId();
            String key = (rId == null || rId.isBlank()) ? "__other__" : rId.trim().toLowerCase();
            byRange.computeIfAbsent(key, k -> new ArrayList<>()).add(product);
        }

        List<RangeGroup> groups = new ArrayList<>();
        for (String rangeId : RANGE_ORDER) {
            List<Product> products = byRange.get(rangeId);
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

    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }
}
