package pl.bartlomiej.apiservice.shiptracking.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.messaging.MessageListenerContainer;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;
import pl.bartlomiej.apiservice.common.sseemission.broadcaster.SseBroadcaster;
import pl.bartlomiej.apiservice.common.sseemission.streamer.AbstractMongoChangeStreamer;
import pl.bartlomiej.apiservice.common.util.MongoDBConstants;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

@Component("shipTrackMongoChangeStreamer")
public class ShipTrackMongoChangeStreamer extends AbstractMongoChangeStreamer {
    private final SseBroadcaster sseBroadcaster;

    public ShipTrackMongoChangeStreamer(@Qualifier("shipTrackInMemorySseBroadcaster") SseBroadcaster sseBroadcaster,
                                        MessageListenerContainer messageListenerContainer) {
        super(messageListenerContainer);
        this.sseBroadcaster = sseBroadcaster;
    }

    @Override
    protected void registerListeningSubscription(MessageListenerContainer messageListenerContainer) {
        AggregationOperation match = match(
                Criteria.where(MongoDBConstants.OPERATION_TYPE).is(MongoDBConstants.INSERT)
        );
        // todo implement pipeline and register it in the container
    }
}
