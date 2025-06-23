package pl.bartlomiej.apiservice.common.sseemission.broadcaster;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.bartlomiej.apiservice.common.sseemission.emissionmanager.SseEmissionManager;

@Configuration
public class SseBroadcastersConfig {

    @Bean
    SseBroadcaster shipTrackInMemorySseBroadcaster(@Qualifier("shipTrackInMemorySseEmissionManager") SseEmissionManager sseEmissionManager) {
        return new DefaultSseBroadcaster(sseEmissionManager);
    }
}
