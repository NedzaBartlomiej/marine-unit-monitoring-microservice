package pl.bartlomiej.apiservice.common.sseemission.emissionmanager;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.bartlomiej.apiservice.common.sseemission.streamer.SseStreamer;

@Configuration
public class SseEmissionManagersConfig {

    @Bean
    SseEmissionManager shipTrackInMemorySseEmissionManager(@Qualifier("shipTrackMongoChangeStreamer") ObjectProvider<SseStreamer> streamerObjectProvider) {
        return new InMemorySseEmissionManager(0L, streamerObjectProvider);
    }
}
