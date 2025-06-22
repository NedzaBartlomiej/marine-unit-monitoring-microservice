package pl.bartlomiej.apiservice.shiptracking.service.sseemission;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.bartlomiej.apiservice.common.seeemission.SseBroadcaster;
import pl.bartlomiej.apiservice.common.seeemission.SseEmissionManager;
import pl.bartlomiej.apiservice.shiptracking.ShipTrack;

import java.io.IOException;

@Component
@Slf4j
public class ShipTrackSseBroadcaster implements SseBroadcaster<ShipTrack> {

    private final SseEmissionManager sseEmissionManager;

    public ShipTrackSseBroadcaster(@Qualifier("shipTrackInMemorySseEmissionManager") SseEmissionManager sseEmissionManager) {
        this.sseEmissionManager = sseEmissionManager;
    }

    @Override
    public void emitForAll(ShipTrack eventObject) {
        log.info("Emitting a new ShipTrack to all subscribers.");
        for (SseEmitter emitter : this.sseEmissionManager.getEmitters()) {
            Thread.startVirtualThread(() -> {
                try {
                    emitter.send(eventObject);
                } catch (IOException e) {
                    log.error("Something go wrong during emitting a ShipTrack. Event details: {}.", eventObject, e);
                }
            });
        }
    }
}
