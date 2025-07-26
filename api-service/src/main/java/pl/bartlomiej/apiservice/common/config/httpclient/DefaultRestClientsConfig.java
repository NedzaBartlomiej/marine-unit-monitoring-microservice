package pl.bartlomiej.apiservice.common.config.httpclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class DefaultRestClientsConfig {
    @Bean
    RestClient defaultRestClient() {
        return RestClient.builder().build();
    }
}
