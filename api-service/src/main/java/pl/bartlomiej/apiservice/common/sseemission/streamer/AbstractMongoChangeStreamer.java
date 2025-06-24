package pl.bartlomiej.apiservice.common.sseemission.streamer;

import org.springframework.data.mongodb.core.messaging.MessageListenerContainer;

/**
 * Abstract base class for MongoDB change stream SSE streamers.
 * Provides a common {@link MessageListenerContainer} and a template method {@link #startStream()}
 * that ensures the container is started only once.
 */
public abstract class AbstractMongoChangeStreamer implements SseStreamer {

    private final MessageListenerContainer messageListenerContainer;

    protected AbstractMongoChangeStreamer(MessageListenerContainer messageListenerContainer) {
        this.messageListenerContainer = messageListenerContainer;
    }

    /**
     * Starts the underlying {@link MessageListenerContainer} if it is not already running,
     * then delegates to {@link #registerListeningSubscription(MessageListenerContainer)} for the subclass to register its subscriptions.
     * This method is final to prevent subclasses from overriding the start logic.
     */
    @Override
    public final void startStream() {
        if (!this.messageListenerContainer.isRunning()) {
            this.messageListenerContainer.start();
        }
        this.registerListeningSubscription(this.messageListenerContainer);
    }

    /**
     * Subclasses implement this method to register their specific change stream subscriptions
     * on the provided {@link MessageListenerContainer}.
     *
     * @param messageListenerContainer the main MessageListenerContainer used for registering subscriptions
     */
    protected abstract void registerListeningSubscription(MessageListenerContainer messageListenerContainer);
}
