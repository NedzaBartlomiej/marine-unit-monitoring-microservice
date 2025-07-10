package pl.bartlomiej.apiservice.common.sseemission.broadcaster;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.bartlomiej.apiservice.common.sseemission.emissionmanager.SseEmissionManager;
import pl.bartlomiej.apiservice.shiptracking.ShipTrack;

@Configuration
public class SseBroadcastersConfig {

    @Bean
    SseBroadcaster<ShipTrack> shipTrackInMemorySseBroadcaster(@Qualifier("shipTrackInMemorySseEmissionManager") SseEmissionManager sseEmissionManager) {
        return new DefaultSseBroadcaster<>(sseEmissionManager);
    }
}
