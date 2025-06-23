package pl.bartlomiej.apiservice.common.sseemission.broadcaster;

public interface SseBroadcaster {
    void emitForAll(Object eventObject);
}
