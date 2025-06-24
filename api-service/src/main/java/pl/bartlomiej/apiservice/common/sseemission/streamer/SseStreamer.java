package pl.bartlomiej.apiservice.common.sseemission.streamer;

/**
 * Represents a streamer that emits SSE events from a source such as a MongoDB change stream.
 * <p>
 * The {@link #startStream()} method should be called only once per streamer instance to
 * initialize and start the streaming process.
 */
public interface SseStreamer {

    /**
     * Starts the stream, initializing any necessary resources.
     * This method is intended to be invoked only once per specific streamer instance.
     */
    void startStream();
}
