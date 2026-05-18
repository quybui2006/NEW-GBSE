package demo.config;

import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class EmailConfig {

    /**
     * Dedicated RestTemplate for outbound email API calls.
     * Connect timeout: 5 s — fail fast if Resend is unreachable.
     * Read timeout: 10 s — a slow response should not hold the async thread.
     * Without these, a hung Resend API blocks the thread until the OS TCP timeout
     * (~2 minutes on Linux), starving the async executor under load.
     */
    @Bean(name = "emailRestTemplate")
    public RestTemplate emailRestTemplate(RestTemplateBuilder builder) {
        return builder
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(10))
                .build();
    }
}
