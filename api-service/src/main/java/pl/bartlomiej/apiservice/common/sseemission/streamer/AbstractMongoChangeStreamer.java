package pl.bartlomiej.apiservice.common.sseemission.streamer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.messaging.MessageListenerContainer;
import org.springframework.data.mongodb.core.messaging.Subscription;

@Slf4j
public abstract class AbstractMongoChangeStreamer implements SseStreamer {

    private final MessageListenerContainer messageListenerContainer;
    private volatile Subscription subscription;

    protected AbstractMongoChangeStreamer(MessageListenerContainer messageListenerContainer) {
        this.messageListenerContainer = messageListenerContainer;
    }


    @Override
    public final void initStream() {
        if (this.subscription != null && this.subscription.isActive()) {
            log.debug("There's an active subscription in requested SseStreamer.");
            return;
        }

        synchronized (this) {
            if (this.subscription != null && this.subscription.isActive()) {
                log.debug("There's an active subscription in requested SseStreamer.");
                return;
            }

            if (!this.messageListenerContainer.isRunning()) {
                this.messageListenerContainer.start();
                log.debug("Starting MongoDB Change Stream Message Listener Container.");
            }

            log.debug("Registering listening subscription for '{}'.", this.getClass().getSimpleName());
            this.subscription = this.registerListeningSubscription(this.messageListenerContainer);
        }
    }


    protected abstract Subscription registerListeningSubscription(MessageListenerContainer messageListenerContainer);
}
