package pl.bartlomiej.apiservice.common.sseemission.broadcaster;

public interface SseBroadcaster<T> {
    void emitForAll(T eventObject);
}
