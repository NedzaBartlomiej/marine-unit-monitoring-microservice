package pl.bartlomiej.apiservice.common.sseemission.broadcaster;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.bartlomiej.apiservice.common.sseemission.emissionmanager.SseEmissionManager;

import java.io.IOException;

@Slf4j
public class DefaultSseBroadcaster<T> implements SseBroadcaster<T> {

    private final SseEmissionManager sseEmissionManager;

    public DefaultSseBroadcaster(SseEmissionManager sseEmissionManager) {
        this.sseEmissionManager = sseEmissionManager;
    }

    @Override
    public void emitForAll(T eventObject) {
        log.info("Emitting a new event to all subscribers.");
        for (SseEmitter emitter : this.sseEmissionManager.getEmitters()) {
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
