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
public class Product {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ten", columnDefinition = "varchar(255)")
    private String name;

    @Column(name = "mo_ta", columnDefinition = "text")
    private String description;

    @Column(columnDefinition = "varchar(500)")
    private String shortText;

    @Column(name = "hinh_anh", columnDefinition = "varchar(255)")
    private String imagePath;

    @Column(name = "danh_muc", columnDefinition = "varchar(100)")
    private String category;

    @Column(columnDefinition = "varchar(100)")
    private String brand;

    @Column(columnDefinition = "varchar(500)")
    private String link;

    // Groups this product under a named range (e.g. "grundfos", "oceancooling", "partners").
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

    // JSON array of {label, url} objects — datasheets and selection guides.
    @Column(columnDefinition = "text")
    private String documents;

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

    @Transient
    public List<DocEntry> getDocumentsList() {
        if (documents == null || documents.isBlank()) return Collections.emptyList();
        try {
            return mapper.readValue(documents, new TypeReference<List<DocEntry>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * Returns a usable image URL for templates.
     * Seeded products store the full path ("/images/pump1.jpg"); uploaded
     * products store just the filename ("uuid_name.jpg").
     */
    @Transient
    public String getImage() {
        if (imagePath == null || imagePath.isBlank()) return "/images/placeholder.png";
        if (imagePath.startsWith("/")) return imagePath;
        return "/uploads/" + imagePath;
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

    public static class DocEntry {
        public String label;
        public String url;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getShortText() { return shortText; }
    public void setShortText(String shortText) { this.shortText = shortText; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public String getRangeId() { return rangeId; }
    public void setRangeId(String rangeId) { this.rangeId = rangeId; }

    public String getApplications() { return applications; }
    public void setApplications(String applications) { this.applications = applications; }

    public String getFeatures() { return features; }
    public void setFeatures(String features) { this.features = features; }

    public String getSpecs() { return specs; }
    public void setSpecs(String specs) { this.specs = specs; }

    public String getDocuments() { return documents; }
    public void setDocuments(String documents) { this.documents = documents; }
}
