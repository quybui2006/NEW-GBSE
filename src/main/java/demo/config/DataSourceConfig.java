package demo.config;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        String url = System.getenv("DATABASE_URL");
        String privateUrl = System.getenv("DATABASE_PRIVATE_URL");

        String rawUrl = (privateUrl != null && !privateUrl.isEmpty()) ? privateUrl : url;

        if (rawUrl == null || rawUrl.isEmpty()) {
            throw new RuntimeException("Không tìm thấy DATABASE_URL!");
        }

        if (!rawUrl.startsWith("jdbc:")) {
            rawUrl = "jdbc:" + rawUrl;
        }

        String username = System.getenv("PGUSER");
        String password = System.getenv("PGPASSWORD");

        return DataSourceBuilder.create()
                .url(rawUrl)
                .username(username)
                .password(password)
                .driverClassName("org.postgresql.Driver")
                .build();
    }
}