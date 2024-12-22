package pl.bartlomiej.apiservice.common.config.webclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class DefaultWebClientConfig {
    @Bean
    WebClient defaultWebClient() {
        return WebClient.builder().build();
    }
}
