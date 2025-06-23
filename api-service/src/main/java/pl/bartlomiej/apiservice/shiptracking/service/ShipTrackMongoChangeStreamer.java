package pl.bartlomiej.apiservice.shiptracking.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.messaging.ChangeStreamRequest;
import org.springframework.data.mongodb.core.query.Criteria;
import pl.bartlomiej.apiservice.common.sseemission.SseStreamer;
import pl.bartlomiej.apiservice.common.sseemission.broadcaster.SseBroadcaster;
import pl.bartlomiej.apiservice.common.util.MongoDBConstants;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

// todo - implement
public class ShipTrackMongoChangeStreamer implements SseStreamer {
    private final SseBroadcaster sseBroadcaster;

    public ShipTrackMongoChangeStreamer(@Qualifier("shipTrackInMemorySseBroadcaster") SseBroadcaster sseBroadcaster) {
        this.sseBroadcaster = sseBroadcaster;
    }

    // todo - invent some logic to initialize streamer lazy
    @Override
    public void initStream() {

    }

    private ChangeStreamRequest.ChangeStreamRequestOptions buildPipeline() {
        AggregationOperation match = match(
                Criteria.where(MongoDBConstants.OPERATION_TYPE).is(MongoDBConstants.INSERT)
        );

    }
}
