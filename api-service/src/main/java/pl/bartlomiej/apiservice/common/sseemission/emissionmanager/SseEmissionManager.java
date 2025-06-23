package pl.bartlomiej.apiservice.common.sseemission.emissionmanager;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collection;

public interface SseEmissionManager {
    Collection<SseEmitter> getEmitters();

    SseEmitter getOrCreateEmitter(String identifier);
}
