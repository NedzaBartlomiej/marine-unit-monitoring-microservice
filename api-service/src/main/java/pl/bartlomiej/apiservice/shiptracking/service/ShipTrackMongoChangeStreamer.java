package pl.bartlomiej.apiservice.shiptracking.service;

import com.mongodb.client.model.changestream.ChangeStreamDocument;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
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
import pl.bartlomiej.apiservice.shiptracking.ShipTrackConstants;

@Component("shipTrackMongoChangeStreamer")
public final class ShipTrackMongoChangeStreamer extends AbstractMongoChangeStreamer {
    private final SseBroadcaster<ShipTrack> sseBroadcaster;
    private final String springDataMongodbDatabase;

    public ShipTrackMongoChangeStreamer(@Qualifier("shipTrackInMemorySseBroadcaster") SseBroadcaster<ShipTrack> sseBroadcaster,
                                        MessageListenerContainer messageListenerContainer,
                                        @Value("${spring.data.mongodb.database}") String springDataMongodbDatabase) {
        super(messageListenerContainer);
        this.sseBroadcaster = sseBroadcaster;
        this.springDataMongodbDatabase = springDataMongodbDatabase;
    }

    @Override
    protected Subscription registerListeningSubscription(MessageListenerContainer messageListenerContainer) {
        return messageListenerContainer.register(
                new ChangeStreamRequest<>(this.getListener(), this.getRequestOptions()),
                ShipTrack.class
        );
    }

    private MessageListener<ChangeStreamDocument<Document>, ShipTrack> getListener() {
        return message -> this.sseBroadcaster.emitForAll(message.getBody());
    }

    private ChangeStreamRequest.ChangeStreamRequestOptions getRequestOptions() {
        AggregationOperation pipeline = Aggregation.match(
                Criteria.where(MongoDBConstants.OPERATION_TYPE).is(MongoDBConstants.INSERT)
        );
        Aggregation aggregation = Aggregation.newAggregation(pipeline);
        ChangeStreamOptions options = ChangeStreamOptions.builder()
                .filter(aggregation)
                .build();
        return new ChangeStreamRequest.ChangeStreamRequestOptions(
                this.springDataMongodbDatabase,
                ShipTrackConstants.SHIP_TRACKS_COLLECTION,
                options
        );
    }
}
