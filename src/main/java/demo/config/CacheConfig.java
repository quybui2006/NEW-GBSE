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
 * Products are managed via Flyway migrations; the cache refreshes on restart.
 */
@Configuration
@EnableCaching
public class CacheConfig {
    // Caffeine is auto-configured via application.properties.
    // No @Bean definition needed — Spring Boot wires it from the spec string.
}
