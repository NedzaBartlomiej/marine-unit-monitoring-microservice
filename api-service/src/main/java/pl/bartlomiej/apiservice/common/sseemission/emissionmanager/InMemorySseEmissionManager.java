package pl.bartlomiej.apiservice.common.sseemission.emissionmanager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.bartlomiej.apiservice.common.sseemission.streamer.SseStreamer;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class InMemorySseEmissionManager implements SseEmissionManager {


    private final Map<String, SseEmitter> storage = new ConcurrentHashMap<>();
    private final long emissionTimeout;
    private final AtomicBoolean streamRunning = new AtomicBoolean(false);
    private final ObjectProvider<SseStreamer> streamerObjectProvider;

    public InMemorySseEmissionManager(long emissionTimeout, ObjectProvider<SseStreamer> streamerObjectProvider) {
        this.emissionTimeout = emissionTimeout;
        this.streamerObjectProvider = streamerObjectProvider;
    }

    /***
     * @return an unmodifiable collection which is live-connected with the emitters-storage values.
     */
    @Override
    public Collection<SseEmitter> getEmitters() {
        return Collections.unmodifiableCollection(this.storage.values());
    }

    /***
     * @param identifier There is only one emitter per identifier.
     * @return A new SseEmitter - if there was an emitter before the new one was created, it will be deleted and completed.
     * <p>
     * This method lazily starts the streaming by calling `initStream()` on the SseStreamer bean
     * when the first emitter is created (controlled by streamRunning flag).
     * <p>
     * Note: This method may throw BeansException (e.g., NoSuchBeanDefinitionException) if the SseStreamer bean
     * is not present in the Spring context at runtime when getObject() is called.
     */
    @Override
    public SseEmitter getOrCreateEmitter(String identifier) {
        SseEmitter newEmitter = this.create(identifier);
        SseEmitter previous = this.storage.put(identifier, newEmitter);
        if (previous != null) previous.complete();

        if (this.streamRunning.compareAndSet(false, true)) {
            this.streamerObjectProvider.getObject().startStream();
        }

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