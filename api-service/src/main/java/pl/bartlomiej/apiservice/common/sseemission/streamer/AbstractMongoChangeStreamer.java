package pl.bartlomiej.apiservice.common.sseemission.streamer;

import org.springframework.data.mongodb.core.messaging.MessageListenerContainer;
import org.springframework.data.mongodb.core.messaging.Subscription;

public abstract class AbstractMongoChangeStreamer implements SseStreamer {

    private final MessageListenerContainer messageListenerContainer;
    private volatile Subscription subscription;

    protected AbstractMongoChangeStreamer(MessageListenerContainer messageListenerContainer) {
        this.messageListenerContainer = messageListenerContainer;
    }


    @Override
    public final void initStream() {
        if (this.subscription != null) return;

        synchronized (this) {
            if (this.subscription != null) return;

            if (!this.messageListenerContainer.isRunning()) {
                this.messageListenerContainer.start();
            }

            this.subscription = this.registerListeningSubscription(this.messageListenerContainer);
        }
    }


    protected abstract Subscription registerListeningSubscription(MessageListenerContainer messageListenerContainer);
}
