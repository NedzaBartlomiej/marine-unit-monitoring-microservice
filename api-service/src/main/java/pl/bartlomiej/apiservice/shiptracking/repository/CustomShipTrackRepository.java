package pl.bartlomiej.apiservice.shiptracking.repository;

import pl.bartlomiej.apiservice.shiptracking.ShipTrack;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CustomShipTrackRepository {
    List<ShipTrack> findByMmsiInAndReadingTimeBetween(List<String> mmsis, LocalDateTime from, LocalDateTime to);

    Map<String, ShipTrack> getLatestShipTracksForMmsis(Set<String> mmsis);
}
