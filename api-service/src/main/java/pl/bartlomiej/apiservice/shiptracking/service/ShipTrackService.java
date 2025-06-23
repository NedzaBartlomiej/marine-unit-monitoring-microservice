package pl.bartlomiej.apiservice.shiptracking.service;

import pl.bartlomiej.apiservice.shiptracking.ShipTrack;

import java.time.LocalDateTime;
import java.util.List;

public interface ShipTrackService {
    List<ShipTrack> getShipTracks(List<String> mmsis, LocalDateTime from, LocalDateTime to);

    void clearShipHistory(String mmsi);
}
