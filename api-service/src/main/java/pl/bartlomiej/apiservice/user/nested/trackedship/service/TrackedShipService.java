package pl.bartlomiej.apiservice.user.nested.trackedship.service;

import pl.bartlomiej.apiservice.user.nested.trackedship.TrackedShip;

import java.util.List;

public interface TrackedShipService {
    List<TrackedShip> getTrackedShips(String id);

    TrackedShip addTrackedShip(String id, String mmsi);

    void removeTrackedShip(String id, String mmsi);
}
