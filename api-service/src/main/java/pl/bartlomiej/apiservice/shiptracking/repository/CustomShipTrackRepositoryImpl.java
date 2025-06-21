package pl.bartlomiej.apiservice.shiptracking.repository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pl.bartlomiej.apiservice.shiptracking.ShipTrack;
import pl.bartlomiej.apiservice.shiptracking.ShipTrackConstants;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.domain.Sort.by;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
class CustomShipTrackRepositoryImpl implements CustomShipTrackRepository {

    private final MongoTemplate mongoTemplate;

    public CustomShipTrackRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<ShipTrack> findByMmsiInAndReadingTimeBetween(List<String> mmsis, LocalDateTime from, LocalDateTime to) {
        Query q = new Query().addCriteria(
                Criteria
                        .where(ShipTrackConstants.MMSI).in(mmsis)
                        .and(ShipTrackConstants.READING_TIME).gte(from).lte(to)
        );
        return mongoTemplate.find(q, ShipTrack.class);
    }

    @Override
    public ShipTrack getLatest(String mmsi) {
        Query q = new Query();
        q.addCriteria(where(ShipTrackConstants.MMSI).is(mmsi));
        q.with(by(DESC, ShipTrackConstants.READING_TIME));
        q.limit(1);
        return mongoTemplate.find(q, ShipTrack.class).getFirst();
    }
}