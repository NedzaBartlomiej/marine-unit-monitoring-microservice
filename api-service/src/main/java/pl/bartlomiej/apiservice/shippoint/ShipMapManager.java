package pl.bartlomiej.apiservice.shippoint;

import java.util.List;

public interface ShipMapManager {
    List<ShipPoint> getActiveShipPoints();

    List<String> getActiveShipMmsis();

    boolean isShipPointActive(String mmsi);

    String getShipPointName(String mmsi);

    void refreshMap();
}