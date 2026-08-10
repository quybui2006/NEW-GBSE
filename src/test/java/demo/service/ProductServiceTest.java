package demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import demo.model.CategoryGroup;
import demo.model.Product;
import demo.model.RangeGroup;
import demo.repository.ProductRepository;

/** Unit tests for product grouping/retrieval logic — no Spring context needed. */
class ProductServiceTest {

    private final ProductRepository repository = mock(ProductRepository.class);
    private final ProductService service = new ProductService(repository);

    private static Product product(String name, String rangeId) {
        return product(name, null, rangeId);
    }

    private static Product product(String name, String category, String rangeId) {
        Product p = new Product();
        p.setName(name);
        p.setCategory(category);
        p.setRangeId(rangeId);
        return p;
    }

    @Test
    void getRangeGroups_returnsCanonicalOrderRegardlessOfInputOrder() {
        List<RangeGroup> groups = service.getRangeGroups(List.of(
            product("Plate Heat Exchanger", "phe"),
            product("DN15-32", "Energy Meter", "metering"),
            product("Motorized Globe Valve", "control-valve-hvac"),
            product("N550 - Surge Anticipation Valve", "Anti water hammer valves", "automatic-control-valve")
        ));

        assertEquals(4, groups.size());
        assertEquals("automatic-control-valve", groups.get(0).getId());
        assertEquals("control-valve-hvac", groups.get(1).getId());
        assertEquals("metering", groups.get(2).getId());
        assertEquals("phe", groups.get(3).getId());
    }

    @Test
    void getRangeGroups_appendsUnknownRangesAfterCanonicalOnes() {
        List<RangeGroup> groups = service.getRangeGroups(List.of(
            product("Misc", "experimental"),
            product("Heatpump Air Source", "heatpump")
        ));

        assertEquals(2, groups.size());
        assertEquals("heatpump", groups.get(0).getId());
        assertEquals("experimental", groups.get(1).getId());
    }

    @Test
    void getRangeGroups_splitsProductsIntoChildCategories() {
        List<RangeGroup> groups = service.getRangeGroups(List.of(
            product("DN15-32", "Energy Meter", "metering"),
            product("DN15-100", "Energy Meter", "metering"),
            product("DN15-2000", "Flowmeter", "metering")
        ));

        List<CategoryGroup> categories = groups.get(0).getCategories();
        assertEquals(2, categories.size());
        assertEquals("Energy Meter", categories.get(0).getName());
        assertEquals(2, categories.get(0).getProducts().size());
        assertEquals("Flowmeter", categories.get(1).getName());
        assertEquals(1, categories.get(1).getProducts().size());
        // The flat list stays intact for the navbar flyout.
        assertEquals(3, groups.get(0).getProducts().size());
    }

    @Test
    void getRangeGroups_putsUncategorisedProductsFirstUnderBlankGroup() {
        List<RangeGroup> groups = service.getRangeGroups(List.of(
            product("CAHP-1.5HP", "Heatpump all in one", "heatpump"),
            product("Heatpump Air Source", null, "heatpump")
        ));

        List<CategoryGroup> categories = groups.get(0).getCategories();
        assertEquals(2, categories.size());
        assertEquals("", categories.get(0).getName());
        assertEquals("Heatpump Air Source", categories.get(0).getProducts().get(0).getName());
        assertEquals("Heatpump all in one", categories.get(1).getName());
    }

    @Test
    void findById_delegatesToRepository() {
        when(repository.findById(1L)).thenReturn(Optional.of(product("DN15-32", "metering")));
        assertTrue(service.findById(1L).isPresent());
    }
}
