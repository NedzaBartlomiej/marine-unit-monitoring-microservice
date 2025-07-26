package pl.bartlomiej.apiservice.shiptracking.service;

import pl.bartlomiej.apiservice.shiptracking.ShipTrack;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface ShipTrackService {
    List<ShipTrack> getShipTracks(Set<String> mmsis, LocalDateTime from, LocalDateTime to);
}
