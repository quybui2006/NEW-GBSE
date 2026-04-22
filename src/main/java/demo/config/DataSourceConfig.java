package demo.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @Bean
    public DataSource dataSource() {
        String url = databaseUrl;
        if (url != null && !url.isEmpty() && !url.startsWith("jdbc:")) {
            url = "jdbc:" + url;
        }
        return DataSourceBuilder.create()
                .url(url)
                .driverClassName("org.postgresql.Driver")
                .build();
    }
}