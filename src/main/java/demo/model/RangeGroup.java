package demo.model;

import java.util.List;

/**
 * View-layer DTO that groups SanPham records under a named brand range.
 * Mirrors the structure Data.Range provided, but backed by the database.
 */
public class RangeGroup {

    private final String id;
    private final String brand;
    private final String tagline;
    private final List<SanPham> products;

    public RangeGroup(String id, String brand, String tagline, List<SanPham> products) {
        this.id       = id;
        this.brand    = brand;
        this.tagline  = tagline;
        this.products = products;
    }

    public String getId()             { return id; }
    public String getBrand()          { return brand; }
    public String getTagline()        { return tagline; }
    public List<SanPham> getProducts(){ return products; }
}
