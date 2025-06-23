package pl.bartlomiej.apiservice.common.sseemission.emissionmanager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class InMemorySseEmissionManager implements SseEmissionManager {

    private final Map<String, SseEmitter> storage = new ConcurrentHashMap<>();
    private final long emissionTimeout;

    public InMemorySseEmissionManager(long emissionTimeout) {
        this.emissionTimeout = emissionTimeout;
    }

    @Override
    public Collection<SseEmitter> getEmitters() {
        return this.storage.values();
    }

    /***
     * @param identifier There is only one emitter per identifier.
     * @return A new SseEmitter - if there was an emitter before the new one was created, it will be deleted and completed.
     */
    @Override
    public SseEmitter getOrCreateEmitter(String identifier) {
        SseEmitter newEmitter = this.create(identifier);
        SseEmitter previous = this.storage.put(identifier, newEmitter);
        if (previous != null) previous.complete();

        return newEmitter;
    }

    private SseEmitter create(String identifier) {
        log.info("Creating a new SseEmitter.");
        SseEmitter sseEmitter = new SseEmitter(this.emissionTimeout);
        sseEmitter.onCompletion(() -> {
            log.info("SseEmitter connection completed - deleting SseEmitter.");
            this.storage.remove(identifier);
        });
        sseEmitter.onError(throwable -> {
            log.error("An error occurred on the SseEmitter connection: {} - deleting SseEmitter.", throwable.toString());
            this.storage.remove(identifier);
        });
        sseEmitter.onTimeout(() -> {
            log.info("SseEmitter connection timed out - deleting SeeEmitter.");
            this.storage.remove(identifier);
        });

        return sseEmitter;
    }
}