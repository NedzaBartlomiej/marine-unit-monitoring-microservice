package pl.bartlomiej.apiservice.shiptracking.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.bartlomiej.apiservice.ais.service.AisService;
import pl.bartlomiej.apiservice.common.exception.apiexception.RecordNotFoundException;
import pl.bartlomiej.apiservice.common.util.MongoDBConstants;
import pl.bartlomiej.apiservice.point.activepoint.service.ActivePointService;
import pl.bartlomiej.apiservice.shiptracking.ShipTrack;
import pl.bartlomiej.apiservice.shiptracking.ShipTrackConstants;
import pl.bartlomiej.apiservice.shiptracking.helper.DateRangeHelper;
import pl.bartlomiej.apiservice.shiptracking.repository.CustomShipTrackRepository;
import pl.bartlomiej.apiservice.shiptracking.repository.MongoShipTrackRepository;
import pl.bartlomiej.apiservice.user.nested.trackedship.TrackedShip;
import pl.bartlomiej.apiservice.user.nested.trackedship.service.TrackedShipService;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Service
class ShipTrackServiceImpl implements ShipTrackService {

    private static final Logger log = LoggerFactory.getLogger(ShipTrackServiceImpl.class);
    private final AisService aisService;
    private final TrackedShipService trackedShipService;
    private final MongoShipTrackRepository mongoShipTrackRepository;
    private final CustomShipTrackRepository customShipTrackRepository;
    private final MongoTemplate mongoTemplate;
    private final ActivePointService activePointService;

    public ShipTrackServiceImpl(
            AisService aisService, TrackedShipService trackedShipService,
            MongoShipTrackRepository mongoShipTrackRepository,
            CustomShipTrackRepository customShipTrackRepository,
            MongoTemplate mongoTemplate,
            ActivePointService activePointService) {
        this.aisService = aisService;
        this.trackedShipService = trackedShipService;
        this.mongoShipTrackRepository = mongoShipTrackRepository;
        this.customShipTrackRepository = customShipTrackRepository;
        this.mongoTemplate = mongoTemplate;
        this.activePointService = activePointService;
    }


    // TRACK HISTORY - operations

    public Flux<ShipTrack> getShipTrackHistory(String userId, LocalDateTime from, LocalDateTime to) {


        return trackedShipService.getTrackedShips(userId)
                .map(TrackedShip::mmsi)
                .collectList()
                .flatMapMany(mmsis -> {

                    // PROCESS DATE RANGE
                    DateRangeHelper dateRangeHelper = new DateRangeHelper(from, to);

                    // DB RESULT STREAM
                    Flux<ShipTrack> dbStream = customShipTrackRepository
                            .findByMmsiInAndReadingTimeBetween(mmsis, dateRangeHelper.from(), dateRangeHelper.to());

                    // CHANGE STREAM - used when the client wants to track the future
                    if (dateRangeHelper.to().isAfter(now()) || to == null) {

                        AggregationOperation match;
                        if (to == null) {
                            match = match(
                                    Criteria.where(MongoDBConstants.OPERATION_TYPE).is(MongoDBConstants.INSERT)
                                            .and(ShipTrackConstants.MMSI).in(mmsis)
                            );
                        } else {
                            match = match(
                                    Criteria.where(MongoDBConstants.OPERATION_TYPE).is(MongoDBConstants.INSERT)
                                            .and(ShipTrackConstants.MMSI).in(mmsis)
                                            .and(ShipTrackConstants.READING_TIME).lte(dateRangeHelper.to())
                            );
                        }
                        Aggregation pipeline = newAggregation(match);

                        Flux<ShipTrack> shipTrackStream = changeStream
                                .mapNotNull(ChangeStreamEvent::getBody)
                                .doOnNext(shipTrack ->
                                        log.info("New ShipTrack returning... mmsi: {}", shipTrack.getMmsi())
                                );
                        return dbStream.concatWith(shipTrackStream);
                    } else {
                        return dbStream;
                    }
                });
    }

    @Scheduled(initialDelay = 0, fixedDelayString = "${project-properties.scheduling-delays.in-ms.ship-tracking.saving}")
    public void saveTracksForTrackedShips() {
        this.getShipTracks()
                .forEach(shipTrack -> {
                    try {
                        this.saveNoStationaryTrack(shipTrack);
                    } catch (Exception e) {
                        log.error("Error saving ship track: {}", shipTrack.getMmsi(), e);
                    }
                });
        log.info("Successfully saved tracked ships coordinates.");
    }

    private void saveNoStationaryTrack(ShipTrack shipTrack) {
        ShipTrack latest = customShipTrackRepository.getLatest(shipTrack.getMmsi());
        if ((latest.getX().equals(shipTrack.getX()) && latest.getY().equals(shipTrack.getY()))) {
            log.warn("Ship hasn't changed its position - saving canceled");
        } else {
            mongoShipTrackRepository.save(shipTrack);
        }
    }

    public void clearShipHistory(String mmsi) {
        if (mongoShipTrackRepository.existsById(mmsi)) {
            mongoShipTrackRepository.deleteAllByMmsi(mmsi);
        } else {
            throw new RecordNotFoundException("ShipTrack by mmsi not found.");
        }
    }

    // GET SHIP TRACKS TO SAVE - operations
    private Stream<ShipTrack> getShipTracks() {
        return aisService.fetchShipsByMmsis(this.getShipMmsisToTrack())
                .stream()
                .map(this::mapToShipTrack);
    }

    private List<String> getShipMmsisToTrack() {
        return activePointService.getMmsis();
    }

    private ShipTrack mapToShipTrack(JsonNode ship) {

        final String LONGITUDE = "longitude";
        final String LATITUDE = "latitude";

        return new ShipTrack(
                ship.get(ShipTrackConstants.MMSI).asText(),
                ship.get(LONGITUDE).asDouble(),
                ship.get(LATITUDE).asDouble()
        );
    }

}