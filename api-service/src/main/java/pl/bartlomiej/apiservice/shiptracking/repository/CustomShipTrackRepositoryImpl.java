package pl.bartlomiej.apiservice.shiptracking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pl.bartlomiej.apiservice.shiptracking.ShipTrack;
import pl.bartlomiej.apiservice.shiptracking.ShipTrackConstants;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
class CustomShipTrackRepositoryImpl implements CustomShipTrackRepository {

    private final MongoTemplate mongoTemplate;

    public CustomShipTrackRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<ShipTrack> findByMmsiInAndReadingTimeBetween(Set<String> mmsis, LocalDateTime from, LocalDateTime to) {
        Query q = new Query().addCriteria(
                Criteria
                        .where(ShipTrackConstants.MMSI).in(mmsis)
                        .and(ShipTrackConstants.READING_TIME).gte(from).lte(to)
        );
        return mongoTemplate.find(q, ShipTrack.class);
    }

    @Override
    public Map<String, ShipTrack> getLatestShipTracksForMmsis(Set<String> mmsis) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where(ShipTrackConstants.MMSI).in(mmsis)),
                Aggregation.sort(Sort.Direction.DESC, ShipTrackConstants.READING_TIME),
                Aggregation.group(ShipTrackConstants.MMSI).first(Aggregation.ROOT).as("latestShipTrack"),
                Aggregation.replaceRoot("latestShipTrack")
        );

        List<ShipTrack> results = mongoTemplate.aggregate(aggregation, "shipTrack", ShipTrack.class).getMappedResults();

        return results.stream()
                .collect(Collectors.toMap(ShipTrack::getMmsi, Function.identity()));
    }
}