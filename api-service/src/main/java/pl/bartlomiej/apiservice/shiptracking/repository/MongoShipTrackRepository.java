package pl.bartlomiej.apiservice.shiptracking.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.bartlomiej.apiservice.shiptracking.ShipTrack;

public interface MongoShipTrackRepository extends MongoRepository<ShipTrack, String> {
    boolean existsByMmsi(String mmsi);

    void deleteAllByMmsi(String mmsi);
}
