package demo.model;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "san_pham")
public class SanPham {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "varchar(255)")
    private String ten;

    @Column(columnDefinition = "varchar(500)")
    private String moTa;

    @Column(columnDefinition = "varchar(500)")
    private String shortText;

    @Column(columnDefinition = "varchar(255)")
    private String hinhAnh;

    @Column(columnDefinition = "varchar(100)")
    private String danhMuc;

    @Column(columnDefinition = "varchar(100)")
    private String brand;

    @Column(columnDefinition = "varchar(500)")
    private String link;

    private Double gia;

    // Groups this product under a named range (e.g. "omeax", "gbse", "partners").
    @Column(columnDefinition = "varchar(50)")
    private String rangeId;

    // JSON arrays stored as text, e.g. ["HVAC","Cấp thoát nước"]
    @Column(columnDefinition = "text")
    private String applications;

    @Column(columnDefinition = "text")
    private String features;

    // JSON array of {label, value} objects.
    @Column(columnDefinition = "text")
    private String specs;

    // ── Transient helpers ────────────────────────────────────────────────────

    @Transient
    public List<String> getApplicationsList() {
        return parseStringList(applications);
    }

    @Transient
    public List<String> getFeaturesList() {
        return parseStringList(features);
    }

    @Transient
    public List<SpecEntry> getSpecsList() {
        if (specs == null || specs.isBlank()) return Collections.emptyList();
        try {
            return mapper.readValue(specs, new TypeReference<List<SpecEntry>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // Convenience aliases used by Thymeleaf templates and JS serialization.

    @Transient
    public String getName() { return ten; }

    @Transient
    public String getDescription() { return moTa; }

    @Transient
    public String getCategory() { return danhMuc; }

    /**
     * Returns a usable image URL for templates.
     * Migrated products store the full path ("/images/pump1.jpg"); uploaded
     * products store just the filename ("uuid_name.jpg").
     */
    @Transient
    public String getImage() {
        if (hinhAnh == null || hinhAnh.isBlank()) return "/images/placeholder.png";
        if (hinhAnh.startsWith("/")) return hinhAnh;
        return "/uploads/" + hinhAnh;
    }

    private List<String> parseStringList(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return mapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // ── Inner type for specs ─────────────────────────────────────────────────

    public static class SpecEntry {
        public String label;
        public String value;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public String getShortText() { return shortText; }
    public void setShortText(String shortText) { this.shortText = shortText; }

    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }

    public String getDanhMuc() { return danhMuc; }
    public void setDanhMuc(String danhMuc) { this.danhMuc = danhMuc; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public Double getGia() { return gia; }
    public void setGia(Double gia) { this.gia = gia; }

    public String getRangeId() { return rangeId; }
    public void setRangeId(String rangeId) { this.rangeId = rangeId; }

    public String getApplications() { return applications; }
    public void setApplications(String applications) { this.applications = applications; }

    public String getFeatures() { return features; }
    public void setFeatures(String features) { this.features = features; }

    public String getSpecs() { return specs; }
    public void setSpecs(String specs) { this.specs = specs; }
}
