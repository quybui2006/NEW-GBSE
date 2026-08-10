package demo.model;

import java.util.List;

/**
 * View-layer DTO that groups Product records under a child category inside a
 * RangeGroup. A blank name means the products sit directly under the range
 * with no child category of their own.
 */
public class CategoryGroup {

    private final String name;
    private final List<Product> products;

    public CategoryGroup(String name, List<Product> products) {
        this.name     = name;
        this.products = products;
    }

    public String getName()            { return name; }
    public List<Product> getProducts() { return products; }
}
