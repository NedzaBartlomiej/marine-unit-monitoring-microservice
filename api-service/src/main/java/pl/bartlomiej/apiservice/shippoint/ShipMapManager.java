package pl.bartlomiej.apiservice.shippoint;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// todo: write test that checks if there is only one bean of this type on the application context
public interface ShipMapManager {
    Optional<List<ShipPoint>> getActiveShipPoints();

    Optional<List<String>> getActiveShipMmsis();

    boolean isShipPointActive(String mmsi);

    Optional<String> getShipPointName(String mmsi);

    void refreshMap();

    LocalDateTime getLastRefreshed();

    long getShipPointMapRefreshmentDelay();
}