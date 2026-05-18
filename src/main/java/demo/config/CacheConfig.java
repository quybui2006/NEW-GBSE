package demo.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Activates Spring's caching abstraction.
 * The Caffeine implementation is auto-configured from application.properties:
 *   spring.cache.type=caffeine
 *   spring.cache.caffeine.spec=maximumSize=500,expireAfterWrite=600s
 *
 * Cache "products" holds the full product list for up to 10 minutes.
 * It is evicted on any save or delete in SanPhamService so the public
 * product catalogue always reflects the latest admin changes within seconds.
 */
@Configuration
@EnableCaching
public class CacheConfig {
    // Caffeine is auto-configured via application.properties.
    // No @Bean definition needed — Spring Boot wires it from the spec string.
}
