package demo.model;

import java.util.List;

/** View-layer DTO that groups Product records under a named brand range. */
public class RangeGroup {

    private final String id;
    private final String brand;
    private final String tagline;
    private final List<Product> products;
    private final List<CategoryGroup> categories;

    public RangeGroup(String id, String brand, String tagline,
                      List<Product> products, List<CategoryGroup> categories) {
        this.id         = id;
        this.brand      = brand;
        this.tagline    = tagline;
        this.products   = products;
        this.categories = categories;
    }

    public String getId()                      { return id; }
    public String getBrand()                   { return brand; }
    public String getTagline()                 { return tagline; }
    /** Flat list of every product in the range, used by the navbar flyout. */
    public List<Product> getProducts()         { return products; }
    /** Same products grouped by child category, used by the catalogue page. */
    public List<CategoryGroup> getCategories() { return categories; }
}
