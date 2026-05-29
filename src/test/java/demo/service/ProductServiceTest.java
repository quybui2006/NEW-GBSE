package demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import demo.model.Product;
import demo.model.RangeGroup;
import demo.repository.ProductRepository;

/** Unit tests for product grouping/retrieval logic — no Spring context needed. */
class ProductServiceTest {

    private final ProductRepository repository = mock(ProductRepository.class);
    private final ProductService service = new ProductService(repository);

    private static Product product(String name, String rangeId) {
        Product p = new Product();
        p.setName(name);
        p.setRangeId(rangeId);
        return p;
    }

    @Test
    void getRangeGroups_returnsCanonicalOrderRegardlessOfInputOrder() {
        List<RangeGroup> groups = service.getRangeGroups(List.of(
            product("System", "partners"),
            product("Valve", "oceancooling"),
            product("Pump", "grundfos")
        ));

        assertEquals(3, groups.size());
        assertEquals("grundfos", groups.get(0).getId());
        assertEquals("oceancooling", groups.get(1).getId());
        assertEquals("partners", groups.get(2).getId());
    }

    @Test
    void getRangeGroups_appendsUnknownRangesAfterCanonicalOnes() {
        List<RangeGroup> groups = service.getRangeGroups(List.of(
            product("Misc", "experimental"),
            product("Pump", "grundfos")
        ));

        assertEquals(2, groups.size());
        assertEquals("grundfos", groups.get(0).getId());
        assertEquals("experimental", groups.get(1).getId());
    }

    @Test
    void findById_delegatesToRepository() {
        when(repository.findById(1L)).thenReturn(Optional.of(product("Pump", "grundfos")));
        assertTrue(service.findById(1L).isPresent());
    }
}
