package pl.bartlomiej.apiservice.common.seeemission;

public interface SseBroadcaster<T> {
    void emitForAll(T eventObject);
}
