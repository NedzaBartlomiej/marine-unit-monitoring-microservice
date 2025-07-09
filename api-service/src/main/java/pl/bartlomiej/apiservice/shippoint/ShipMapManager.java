package pl.bartlomiej.apiservice.shippoint;

import java.time.LocalDateTime;
import java.util.List;

// todo: write test that checks if there is only one bean of this type on the application context
public interface ShipMapManager {
    List<ShipPoint> getActiveShipPoints();

    List<String> getActiveShipMmsis();

    boolean isShipPointActive(String mmsi);

    String getShipPointName(String mmsi);

    void refreshMap();

    LocalDateTime lastRefreshed();
}