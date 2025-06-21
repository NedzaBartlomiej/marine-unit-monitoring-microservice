package pl.bartlomiej.apiservice.common.streamemission;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface EmissionManager<T> {

    SseEmitter getOrCreateEmitter(String xApiKey);

    void deleteEmitter(String xApiKey);

    void emitForAll(T eventObject);
}
