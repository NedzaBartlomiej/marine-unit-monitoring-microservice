package pl.bartlomiej.protectionservice.iploginprotection.common.config;

import io.ipinfo.api.IPinfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IpInfoApiConfig {
    @Bean
    IPinfo iPinfo(@Value("${ip-info-api.access-token}") String token) {
        return new IPinfo.Builder()
                .setToken(token)
                .build();
    }
}
