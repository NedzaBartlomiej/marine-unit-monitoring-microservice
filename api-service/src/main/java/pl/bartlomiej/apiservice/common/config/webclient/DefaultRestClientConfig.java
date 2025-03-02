package pl.bartlomiej.apiservice.common.config.webclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class DefaultRestClientConfig {
    @Bean
    RestClient defaultRestClient() {
        return RestClient.builder().build();
    }
}
