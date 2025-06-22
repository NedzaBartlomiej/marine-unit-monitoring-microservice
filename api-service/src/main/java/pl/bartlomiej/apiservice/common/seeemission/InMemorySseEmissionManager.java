package pl.bartlomiej.apiservice.common.seeemission;

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

    @Override
    public SseEmitter getOrCreateEmitter(String identifier) {
        return this.storage.computeIfAbsent(identifier, this::createEmitter);
    }

    private SseEmitter createEmitter(String key) {
        log.info("Creating a new SseEmitter.");
        SseEmitter sseEmitter = new SseEmitter(this.emissionTimeout);
        sseEmitter.onCompletion(() -> {
            log.info("SseEmitter connection completed - deleting SseEmitter.");
            this.storage.remove(key);
        });
        sseEmitter.onError(throwable -> {
            log.error("An error occurred on the SseEmitter connection: {} - deleting SseEmitter.", throwable.toString());
            this.storage.remove(key);
        });
        sseEmitter.onTimeout(() -> {
            log.info("SseEmitter connection timed out - deleting SeeEmitter.");
            this.storage.remove(key);
        });
        return sseEmitter;
    }

    @Override
    public void deleteEmitter(String identifier) {
        log.debug("Deleting SseEmitter.");
        this.storage.remove(identifier);
    }
}