package pl.bartlomiej.apiservice.user.nested.trackedship.service;

import pl.bartlomiej.apiservice.user.nested.trackedship.TrackedShip;
import pl.bartlomiej.apiservice.user.nested.trackedship.TrackedShipResponseDto;

import java.util.Set;

public interface TrackedShipService {
    Set<TrackedShipResponseDto> getTrackedShipsResponse(String id);

    Set<TrackedShip> getTrackedShips(String id);

    TrackedShip addTrackedShip(String id, String mmsi);

    void removeTrackedShip(String id, String mmsi);
}
