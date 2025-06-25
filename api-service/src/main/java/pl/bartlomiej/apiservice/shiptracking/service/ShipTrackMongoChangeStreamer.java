package pl.bartlomiej.apiservice.shiptracking.service;

import com.mongodb.client.model.changestream.ChangeStreamDocument;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.messaging.ChangeStreamRequest;
import org.springframework.data.mongodb.core.messaging.MessageListener;
import org.springframework.data.mongodb.core.messaging.MessageListenerContainer;
import org.springframework.data.mongodb.core.messaging.Subscription;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;
import pl.bartlomiej.apiservice.common.sseemission.broadcaster.SseBroadcaster;
import pl.bartlomiej.apiservice.common.sseemission.streamer.AbstractMongoChangeStreamer;
import pl.bartlomiej.apiservice.common.util.MongoDBConstants;
import pl.bartlomiej.apiservice.shiptracking.ShipTrack;

@Component("shipTrackMongoChangeStreamer")
public final class ShipTrackMongoChangeStreamer extends AbstractMongoChangeStreamer {
    private final SseBroadcaster sseBroadcaster;

    public ShipTrackMongoChangeStreamer(@Qualifier("shipTrackInMemorySseBroadcaster") SseBroadcaster sseBroadcaster,
                                        MessageListenerContainer messageListenerContainer) {
        super(messageListenerContainer);
        this.sseBroadcaster = sseBroadcaster;
    }

    @Override
    protected Subscription registerListeningSubscription(MessageListenerContainer messageListenerContainer) {
        return messageListenerContainer.register(
                new ChangeStreamRequest<>(this.getListener(), this.getOptions()),
                ShipTrack.class
        );
    }

    private MessageListener<ChangeStreamDocument<Document>, ShipTrack> getListener() {
        return this.sseBroadcaster::emitForAll;
    }

    private ChangeStreamRequest.ChangeStreamRequestOptions getOptions() {
        AggregationOperation pipeline = Aggregation.match(
                Criteria.where(MongoDBConstants.OPERATION_TYPE).is(MongoDBConstants.INSERT)
        );
        Aggregation aggregation = Aggregation.newAggregation(pipeline);
        return ChangeStreamRequest.builder()
                .filter(aggregation)
                .build()
                .getRequestOptions();
    }
}
