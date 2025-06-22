package pl.bartlomiej.apiservice.common.seeemission;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collection;

public interface SseEmissionManager {
    Collection<SseEmitter> getEmitters();

    SseEmitter getOrCreateEmitter(String identifier);

    void deleteEmitter(String identifier);
}
