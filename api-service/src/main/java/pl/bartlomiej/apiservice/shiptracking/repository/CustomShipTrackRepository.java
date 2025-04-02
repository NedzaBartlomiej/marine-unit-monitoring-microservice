package pl.bartlomiej.apiservice.shiptracking.repository;

import pl.bartlomiej.apiservice.shiptracking.ShipTrack;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomShipTrackRepository {
    List<ShipTrack> findByMmsiInAndReadingTimeBetween(List<String> mmsis, LocalDateTime from, LocalDateTime to);

    ShipTrack getLatest(String mmsi);
}
