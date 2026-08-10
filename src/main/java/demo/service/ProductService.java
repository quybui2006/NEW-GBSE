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

import demo.model.CategoryGroup;
import demo.model.Product;
import demo.model.RangeGroup;
import demo.repository.ProductRepository;

@Service
public class ProductService {

    private static final List<String> RANGE_ORDER = List.of(
        "automatic-control-valve", "control-valve-hvac", "heatpump", "metering", "phe");

    private static final Map<String, String> RANGE_BRANDS = Map.of(
        "automatic-control-valve", "Automatic Control Valve",
        "control-valve-hvac",      "Control valve HVAC",
        "heatpump",                "Heatpump",
        "metering",                "Metering and Measuring",
        "phe",                     "Plate Heat Exchanger"
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
                groups.add(toRangeGroup(rangeId, products));
            }
        }

        byRange.forEach((rangeId, products) -> {
            if (!RANGE_ORDER.contains(rangeId) && !"__other__".equals(rangeId) && !products.isEmpty()) {
                groups.add(toRangeGroup(rangeId, products));
            }
        });

        return groups;
    }

    // Taglines are pending content — the range header renders the label only.
    private RangeGroup toRangeGroup(String rangeId, List<Product> products) {
        return new RangeGroup(
            rangeId,
            RANGE_BRANDS.getOrDefault(rangeId, rangeId.toUpperCase()),
            "",
            products,
            toCategoryGroups(products)
        );
    }

    /**
     * Splits a range's products by child category, preserving encounter order.
     * Products with no category come first under a blank-named group, so they
     * render directly beneath the range header with no sub-heading.
     */
    private List<CategoryGroup> toCategoryGroups(List<Product> products) {
        Map<String, List<Product>> byCategory = new LinkedHashMap<>();
        for (Product product : products) {
            String category = product.getCategory();
            String key = (category == null || category.isBlank()) ? "" : category.trim();
            byCategory.computeIfAbsent(key, k -> new ArrayList<>()).add(product);
        }

        List<CategoryGroup> categories = new ArrayList<>();
        List<Product> uncategorised = byCategory.remove("");
        if (uncategorised != null) {
            categories.add(new CategoryGroup("", uncategorised));
        }
        byCategory.forEach((name, items) -> categories.add(new CategoryGroup(name, items)));
        return categories;
    }

    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }
}
