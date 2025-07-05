package pl.bartlomiej.apiservice.user.repository;

import pl.bartlomiej.apiservice.user.nested.trackedship.TrackedShip;

import java.util.List;

public interface CustomUserRepository {

    TrackedShip pushTrackedShip(String id, TrackedShip trackedShip);

    void pullTrackedShip(String id, String mmsi);

    List<TrackedShip> getTrackedShips(String id);

}
