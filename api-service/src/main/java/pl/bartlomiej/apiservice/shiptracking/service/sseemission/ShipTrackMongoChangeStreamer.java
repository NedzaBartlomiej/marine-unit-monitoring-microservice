package pl.bartlomiej.apiservice.shiptracking.service.sseemission;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.messaging.ChangeStreamRequest;
import org.springframework.data.mongodb.core.query.Criteria;
import pl.bartlomiej.apiservice.common.seeemission.SseBroadcaster;
import pl.bartlomiej.apiservice.common.util.MongoDBConstants;
import pl.bartlomiej.apiservice.shiptracking.ShipTrack;
import pl.bartlomiej.apiservice.shiptracking.service.ShipTrackService;
import pl.bartlomiej.apiservice.user.nested.trackedship.service.TrackedShipService;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

public class ShipTrackMongoChangeStreamer {
    private final SseBroadcaster<ShipTrack> sseBroadcaster;
    private final MongoTemplate mongoTemplate;
    private final TrackedShipService trackedShipService;

    public ShipTrackMongoChangeStreamer(SseBroadcaster<ShipTrack> sseBroadcaster, MongoTemplate mongoTemplate, ShipTrackService shipTrackService, TrackedShipService trackedShipService) {
        this.sseBroadcaster = sseBroadcaster;
        this.mongoTemplate = mongoTemplate;
        this.trackedShipService = trackedShipService;
    }

    // todo - make these methods as interface methods (SseStreamer),
    //  then they can be used in the manager to control lazy start and stop if there is no emitter
    public void startStream() {

    }

    public void stopStream() {

    }

    private ChangeStreamRequest.ChangeStreamRequestOptions buildPipeline() {
        AggregationOperation match = match(
                Criteria.where(MongoDBConstants.OPERATION_TYPE).is(MongoDBConstants.INSERT)
        );
        // todo
    }
}
