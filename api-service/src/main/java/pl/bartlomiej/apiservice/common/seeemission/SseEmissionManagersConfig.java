package pl.bartlomiej.apiservice.common.seeemission;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SseEmissionManagersConfig {

    @Bean
    SseEmissionManager shipTrackInMemorySseEmissionManager() {
        return new InMemorySseEmissionManager(0L);
    }
}
