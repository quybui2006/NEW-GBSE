package demo.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import demo.model.Product;
import demo.repository.ProductRepository;

@Component
@Order(2)
public class DataMigrationService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataMigrationService.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final Map<String, String> BRAND_TO_RANGE = Map.of(
        "GRUNDFOS",             "grundfos",
        "OCEAN COOLING TOWER",  "oceancooling",
        "PARTNERS",             "partners"
    );

    private final ProductRepository productRepository;

    public DataMigrationService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            seed();
            // After a fresh seed all products have rangeId set — no backfill needed.
            return;
        }
        log.info("[DataMigration] san_pham table already has data — skipping seed.");

        // Only run the backfill query if there are actually rows with null rangeId.
        // This avoids a full table scan on every startup once the data is clean.
        long nullCount = productRepository.findByRangeIdIsNull().size();
        if (nullCount == 0) {
            log.info("[DataMigration] Backfill: all rows already have rangeId — skipping.");
            return;
        }
        backfillRangeIds();
    }

    // ── Seed ────────────────────────────────────────────────────────────────

    private void seed() {
        List<Product> products = buildSeedData();
        log.info("[DataMigration] Table is empty. Seeding {} products...", products.size());
        int count = 0;
        for (Product sp : products) {
            try {
                productRepository.save(sp);
                count++;
            } catch (Exception e) {
                log.error("[DataMigration] Failed to seed '{}': {}", sp.getName(), e.getMessage());
            }
        }
        log.info("[DataMigration] Seed complete — {} products inserted.", count);
    }

    // ── Backfill ─────────────────────────────────────────────────────────────

    private void backfillRangeIds() {
        List<Product> nullRows = productRepository.findByRangeIdIsNull();
        if (nullRows.isEmpty()) {
            log.info("[DataMigration] Backfill: all rows already have rangeId — nothing to update.");
            return;
        }
        log.info("[DataMigration] Backfill: found {} row(s) with null rangeId.", nullRows.size());
        int updated = 0;
        for (Product sp : nullRows) {
            String brand = sp.getBrand() != null ? sp.getBrand().toUpperCase() : "";
            String rangeId = BRAND_TO_RANGE.get(brand);
            if (rangeId != null) {
                sp.setRangeId(rangeId);
                productRepository.save(sp);
                updated++;
            } else {
                log.warn("[DataMigration] Backfill: no rangeId mapping for brand '{}' (id={}) — skipped.",
                         sp.getBrand(), sp.getId());
            }
        }
        log.info("[DataMigration] Backfill complete — {} row(s) updated.", updated);
    }

    // ── Seed data ─────────────────────────────────────────────────────────────
    // Single source of truth for initial product catalogue.
    // Add rows here and they will be inserted on first startup (empty table only).

    private List<Product> buildSeedData() {
        return List.of(
            // ── Grundfos Pumps — Pumps ───────────────────────────────────────
            sp("Booster Pump",           "pumps",  "/images/pump1.jpg", "Grundfos", "grundfos",
               "Bơm tăng áp biến tần",
               "Hệ tăng áp biến tần Grundfos dành cho tòa nhà cao tầng, cấp nước sinh hoạt ổn định, tiết kiệm điện năng tới 40%.",
               arr("Inverter điều khiển PID", "Vỏ inox 304", "Áp suất tối đa 16 bar", "Lưu lượng tới 80 m³/h"),
               arr("Tòa nhà văn phòng", "Khu chung cư", "Khách sạn"),
               specs("Lưu lượng","5 – 80 m³/h", "Cột áp","20 – 160 m", "Công suất","1.5 – 30 kW", "Nguồn","3P / 380V / 50Hz"),
               null),

            sp("Transfer Pump",          "pumps",  "/images/pump2.jpg", "Grundfos", "grundfos",
               "Bơm chuyển nước",
               "Bơm trục ngang công suất lớn, chuyên cấp & chuyển nước giữa các bể chứa.",
               arr("Cánh đồng thau", "Hiệu suất cao IE3", "Vận hành êm < 60 dB"),
               arr("Cấp nước sinh hoạt", "Hệ thống tưới"),
               specs("Lưu lượng","10 – 300 m³/h", "Cột áp","10 – 100 m", "Công suất","2.2 – 75 kW"),
               null),

            sp("HVAC Pump",              "pumps",  "/images/pump3.jpg", "Grundfos", "grundfos",
               "Bơm tuần hoàn HVAC",
               "Bơm tuần hoàn nước lạnh / nóng cho hệ thống chiller và AHU.",
               arr("Mặt bích DN50–DN300", "Thân gang đúc", "Phớt cơ khí"),
               arr("Hệ chiller", "Hệ sưởi sàn"),
               specs("Cỡ kết nối","DN50 – DN300", "Nhiệt độ","-10°C – 120°C"),
               null),

            sp("Drainage Pump",          "pumps",  "/images/pump4.jpg", "Grundfos", "grundfos",
               "Bơm chìm thoát nước",
               "Bơm chìm chuyên thoát nước hố ga, nước thải sinh hoạt.",
               arr("IP68", "Cánh xé rác", "Phao tự động"),
               arr("Tầng hầm", "Trạm bơm thoát"),
               specs("Cấp bảo vệ","IP68", "Lưu lượng","5 – 50 m³/h"),
               null),

            sp("Fire Fighting Pump",     "pumps",  "/images/pump5.jpg", "Grundfos", "grundfos",
               "Bơm chữa cháy UL/FM",
               "Cụm bơm chữa cháy đạt tiêu chuẩn UL/FM cho công trình thương mại.",
               arr("Bơm chính + bù áp", "Tủ điều khiển tự động", "Theo NFPA 20"),
               arr("PCCC nhà xưởng", "PCCC chung cư"),
               specs("Tiêu chuẩn","UL / FM / NFPA 20"),
               null),

            // ── Grundfos Pumps — Valves ──────────────────────────────────────
            sp("General Valves",         "valves", "/images/valve5.jpg", "Grundfos", "grundfos",
               "Van chặn đa năng",
               "Van cổng, van bi, van một chiều dùng cho hệ nước & PCCC.",
               arr("DN15–DN600", "Gang / inox / đồng", "PN16 – PN25"),
               arr("Cấp nước", "PCCC", "HVAC"),
               specs("Kích cỡ","DN15 – DN600", "Áp suất","PN16 – PN25"),
               null),

            sp("Hydraulic Control Valve","valves", "/images/valve3.jpg", "Grundfos", "grundfos",
               "Van điều khiển thủy lực",
               "Van điều khiển thủy lực giảm áp, duy trì áp suất, ngắt khi quá tải.",
               arr("Pilot tự động", "Không cần điện", "Bền 20+ năm"),
               arr("Mạng cấp nước đô thị", "Trạm bơm"),
               null, null),

            sp("Special Application Valve","valves","/images/valve1.jpg","Grundfos","grundfos",
               "Van ứng dụng đặc biệt",
               "Van bướm, van dao chuyên dùng cho môi trường ăn mòn, nước biển.",
               arr("Body inox 316", "Chống ăn mòn", "Wafer / Lug"),
               arr("Hóa chất", "Xử lý nước thải"),
               null, null),

            // ── Ocean Cooling Tower — Valves ─────────────────────────────────
            sp("PICV / Manual Balancing Valve","valves","/images/valve2.jpg","Ocean Cooling Tower","oceancooling",
               "Van cân bằng PICV",
               "Van cân bằng áp suất độc lập (PICV) đảm bảo lưu lượng ổn định cho FCU/AHU.",
               arr("Pre-setting lưu lượng", "Range 0.05–4.0 m³/h", "Đồng thau"),
               arr("Hệ chiller", "FCU văn phòng"),
               null, null),

            sp("Motorized Valve",        "valves", "/images/valve4.jpg", "Ocean Cooling Tower", "oceancooling",
               "Van điều khiển động cơ",
               "Van điều khiển modulating 0–10V tích hợp BMS.",
               arr("Tín hiệu 0–10V / Modbus", "Lực đóng 600N", "Fail-safe"),
               arr("BMS tòa nhà", "Hệ chiller"),
               null, null),

            // ── Ocean Cooling Tower — Heatpump ───────────────────────────────
            sp("Air to Water Heatpump",  "heatpump","/images/ct1.jpg","Ocean Cooling Tower","oceancooling",
               "Heatpump không khí – nước",
               "Bơm nhiệt không khí – nước tạo nước nóng tới 65°C, COP > 4.5.",
               arr("Inverter DC", "R32 thân thiện môi trường", "Công suất 7–80 kW"),
               arr("Khách sạn", "Bể bơi", "Spa"),
               null, null),

            sp("All-in-one Heatpump",    "heatpump","/images/ct2.jpg","Ocean Cooling Tower","oceancooling",
               "Heatpump tích hợp bồn",
               "Bơm nhiệt all-in-one tích hợp bồn 200L, gọn nhẹ cho hộ gia đình.",
               arr("Bồn inox 200L", "WiFi điều khiển", "Plug & play"),
               arr("Biệt thự", "Căn hộ"),
               null, null),

            // ── Ocean Cooling Tower — Measurement ────────────────────────────
            sp("Water Meter (Cold/Hot)", "measure", "/images/valve6.jpg","Ocean Cooling Tower","oceancooling",
               "Đồng hồ nước nóng / lạnh",
               "Đồng hồ đo lưu lượng nước nóng và lạnh, cấp chính xác R160.",
               arr("DN15–DN50", "Chịu nhiệt 90°C", "MID Class 2"),
               arr("Chung cư", "Tòa nhà"),
               null, null),

            sp("Ultrasonic BTU Meter",   "measure", "/images/valve3.jpg","Ocean Cooling Tower","oceancooling",
               "Đồng hồ nhiệt lượng siêu âm",
               "Đồng hồ nhiệt lượng siêu âm, không có chi tiết chuyển động, độ bền cao.",
               arr("Truyền M-Bus / Modbus", "Pin 10 năm", "Cấp 2 EN1434"),
               arr("Tính tiền BTU chung cư", "District cooling"),
               null, null),

            // ── Ocean Cooling Tower — Plate Heat Exchanger ───────────────────
            sp("Plate Heat Exchanger",   "phe",    "/images/ct3.jpg",  "Ocean Cooling Tower","oceancooling",
               "Trao đổi nhiệt dạng tấm",
               "PHE inox 316L, gasket NBR/EPDM, hiệu suất truyền nhiệt cao.",
               arr("Công suất 10 kW – 5 MW", "Inox 316L", "Tháo lắp dễ dàng"),
               arr("Hot water", "Chiller plant", "Industrial"),
               null, null),

            // ── Partners — Systems ───────────────────────────────────────────
            sp("HVAC System",            "hvac",   "/images/pump3.jpg","Partners","partners",
               "Hệ thống điều hòa trung tâm",
               "Giải pháp HVAC hoàn chỉnh từ chiller, AHU, FCU đến hệ điều khiển BMS.",
               arr("Chiller water-cooled / air-cooled", "AHU & FCU", "Tích hợp BMS"),
               arr("Văn phòng", "TTTM", "Bệnh viện"),
               null, "https://www.grundfos.com/vn"),

            sp("Hot Water Supply System","hot",    "/images/ct1.jpg", "Partners","partners",
               "Hệ cấp nước nóng tập trung",
               "Hệ thống cấp nước nóng dùng heatpump + bồn tích, tối ưu năng lượng cho khách sạn.",
               arr("Bồn inox 1000–10000 L", "Cụm bơm tuần hoàn", "Điều khiển tự động"),
               arr("Khách sạn", "Khu nghỉ dưỡng"),
               null, "https://oceancoolingtower.com/"),

            sp("Industrial Water System","indus",  "/images/ct2.jpg", "Partners","partners",
               "Hệ nước công nghiệp",
               "Hệ thống nước làm mát, nước cấp, nước xử lý cho nhà máy sản xuất.",
               arr("Cooling tower", "Hệ lọc đa tầng", "Hóa chất xử lý"),
               arr("Nhà máy thép", "Nhà máy hoá chất"),
               null, null)
        );
    }

    // ── Builder helpers ───────────────────────────────────────────────────────

    private Product sp(String ten, String danhMuc, String hinhAnh, String brand, String rangeId,
                       String shortText, String moTa,
                       String[] features, String[] applications,
                       String[][] specs, String link) {
        Product s = new Product();
        s.setName(ten);
        s.setCategory(danhMuc);
        s.setImagePath(hinhAnh);
        s.setBrand(brand);
        s.setRangeId(rangeId);
        s.setShortText(shortText);
        s.setDescription(moTa);
        s.setLink(link);
        try {
            s.setFeatures(mapper.writeValueAsString(Arrays.asList(features)));
            s.setApplications(mapper.writeValueAsString(Arrays.asList(applications)));
            if (specs != null) {
                List<Map<String, String>> specList = new ArrayList<>();
                for (String[] kv : specs) {
                    specList.add(Map.of("label", kv[0], "value", kv[1]));
                }
                s.setSpecs(mapper.writeValueAsString(specList));
            }
        } catch (Exception e) {
            log.error("[DataMigration] JSON serialization error for '{}': {}", ten, e.getMessage());
        }
        return s;
    }

    private static String[] arr(String... values) {
        return values;
    }

    private static String[][] specs(String... labelValuePairs) {
        String[][] result = new String[labelValuePairs.length / 2][2];
        for (int i = 0; i < labelValuePairs.length; i += 2) {
            result[i / 2] = new String[]{labelValuePairs[i], labelValuePairs[i + 1]};
        }
        return result;
    }
}
