package pl.bartlomiej.apiservice.shippoint;

import java.util.List;
import java.util.Optional;

interface AisApiShipPointAdapter {

    Optional<List<ShipPoint>> getLatestShipPoints();
}
