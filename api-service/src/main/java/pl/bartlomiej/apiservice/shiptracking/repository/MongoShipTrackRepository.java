package pl.bartlomiej.apiservice.shiptracking.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pl.bartlomiej.apiservice.shiptracking.ShipTrack;

public interface MongoShipTrackRepository extends ReactiveMongoRepository<ShipTrack, String> {

}
