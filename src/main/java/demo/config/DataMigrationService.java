package demo.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import demo.model.Data;
import demo.model.SanPham;
import demo.repository.SanPhamRepository;

/**
 * One-time seed that copies all products from Data.java into the san_pham
 * table. Only runs when the table is empty, so it is safe to leave in place —
 * it will never overwrite data the admin has added or edited via the UI.
 *
 * BEFORE deploying to production (Supabase), run:
 *   ALTER TABLE san_pham ADD COLUMN IF NOT EXISTS range_id VARCHAR(50);
 */
@Component
@Order(2)
public class DataMigrationService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataMigrationService.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Override
    public void run(String... args) {
        if (sanPhamRepository.count() > 0) {
            log.info("[DataMigration] san_pham table already has data — skipping seed.");
            return;
        }

        log.info("[DataMigration] Table is empty. Seeding {} products from Data.java...",
                Data.RANGES.stream().mapToInt(r -> r.products.size()).sum());

        int count = 0;
        for (Data.Range range : Data.RANGES) {
            for (Data.Product p : range.products) {
                try {
                    sanPhamRepository.save(toEntity(p, range.id));
                    count++;
                } catch (Exception e) {
                    log.error("[DataMigration] Failed to seed product '{}': {}", p.id, e.getMessage());
                }
            }
        }

        log.info("[DataMigration] Seed complete — {} products inserted.", count);
    }

    private SanPham toEntity(Data.Product p, String rangeId) throws Exception {
        SanPham sp = new SanPham();
        sp.setTen(p.name);
        sp.setMoTa(p.description);
        sp.setShortText(p.shortText);
        sp.setHinhAnh(p.image);          // stored as-is: "/images/pump1.jpg"
        sp.setDanhMuc(p.category);
        sp.setBrand(p.brand);
        sp.setLink(p.link);
        sp.setRangeId(rangeId);

        sp.setFeatures(mapper.writeValueAsString(p.features));
        sp.setApplications(mapper.writeValueAsString(p.applications));

        // Specs: List<Data.Spec> → [{"label":"...","value":"..."}]
        List<Map<String, String>> specsJson = new ArrayList<>();
        for (Data.Spec s : p.specs) {
            specsJson.add(Map.of("label", s.label, "value", s.value));
        }
        sp.setSpecs(mapper.writeValueAsString(specsJson));

        return sp;
    }
}
