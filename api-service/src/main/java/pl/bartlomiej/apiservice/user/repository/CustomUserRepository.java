package pl.bartlomiej.apiservice.user.repository;

import pl.bartlomiej.apiservice.user.nested.trackedship.TrackedShip;

import java.util.Set;

public interface CustomUserRepository {

    TrackedShip pushTrackedShip(String id, TrackedShip trackedShip);

    void pullTrackedShip(String id, String mmsi);

    Set<TrackedShip> getTrackedShips(String id);

    void pushTrustedIpAddress(String id, String ipAddress);

}
