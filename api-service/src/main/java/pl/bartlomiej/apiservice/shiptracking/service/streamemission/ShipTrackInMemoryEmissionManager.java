package pl.bartlomiej.apiservice.shiptracking.service.streamemission;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.bartlomiej.apiservice.common.streamemission.EmissionManager;
import pl.bartlomiej.apiservice.shiptracking.ShipTrack;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ShipTrackInMemoryEmissionManager implements EmissionManager<ShipTrack> {

    private static final Map<String, SseEmitter> storage = new ConcurrentHashMap<>();
    private static final long EMISSION_TIMEOUT = 0L;


    @Override
    public SseEmitter getOrCreateEmitter(String xApiKey) {
        return storage.computeIfAbsent(xApiKey, this::createEmitter);
    }

    private SseEmitter createEmitter(String key) {
        log.info("Creating a new SseEmitter.");
        SseEmitter sseEmitter = new SseEmitter(EMISSION_TIMEOUT);
        sseEmitter.onCompletion(() -> {
            log.info("SseEmitter connection completed - deleting SseEmitter.");
            storage.remove(key);
        });
        sseEmitter.onError(throwable -> {
            log.error("An error occurred on the SseEmitter connection: {} - deleting SseEmitter.", throwable.toString());
            storage.remove(key);
        });
        sseEmitter.onTimeout(() -> {
            log.info("SseEmitter connection timed out - deleting SeeEmitter.");
            storage.remove(key);
        });
        return sseEmitter;
    }

    @Override
    public void deleteEmitter(String xApiKey) {
        log.debug("Deleting ShipTrack SseEmitter.");
        storage.remove(xApiKey);
    }

    @Override
    public void emitForAll(ShipTrack eventObject) {
        log.info("Emitting a new ShipTrack event to all subscribers.");
        for (SseEmitter emitter : storage.values()) {
            Thread.startVirtualThread(() -> {
                try {
                    emitter.send(eventObject);
                } catch (IOException e) {
                    log.error("Something go wrong during emitting an event. Event details: {}.", eventObject, e);
                }
            });
        }
    }
}
