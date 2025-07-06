package pl.bartlomiej.apiservice.shippoint;

import org.springframework.stereotype.Service;
import pl.bartlomiej.apiservice.aisapi.AisShip;
import pl.bartlomiej.apiservice.aisapi.service.AisService;
import pl.bartlomiej.apiservice.geocoding.Position;
import pl.bartlomiej.apiservice.geocoding.service.GeocodeService;

import java.util.List;
import java.util.Objects;

import static pl.bartlomiej.apiservice.aisapi.nested.Geometry.X_COORDINATE_INDEX;
import static pl.bartlomiej.apiservice.aisapi.nested.Geometry.Y_COORDINATE_INDEX;

@Service
class DefaultAisApiShipPointAdapter implements AisApiShipPointAdapter {

    private static final String UNKNOWN_NOT_REPORTED = "UNKNOWN (NOT REPORTED)";
    private final AisService aisService;
    private final GeocodeService geocodeService;

    public DefaultAisApiShipPointAdapter(AisService aisService, GeocodeService geocodeService) {
        this.aisService = aisService;
        this.geocodeService = geocodeService;
    }

    @Override
    public List<ShipPoint> getShipPoints() {
        return aisService.fetchLatestShips().stream()
                .map(this::mapToPoint)
                .toList();
    }

    private ShipPoint mapToPoint(AisShip aisShip) {
        String mayNullName = Objects.requireNonNullElse(aisShip.properties().name(), UNKNOWN_NOT_REPORTED);
        String mayNullDestination = Objects.requireNonNullElse(aisShip.properties().destination(), UNKNOWN_NOT_REPORTED);
        Position shipPosition = this.getShipPosition(aisShip);
        return new ShipPoint(
                aisShip.properties().mmsi().toString(),
                mayNullName,
                aisShip.geometry().coordinates().get(X_COORDINATE_INDEX),
                aisShip.geometry().coordinates().get(Y_COORDINATE_INDEX),
                mayNullDestination,
                shipPosition.x(),
                shipPosition.y()
        );
    }

    private Position getShipPosition(AisShip aisShip) {
        return geocodeService.getAddressCoordinates(aisShip.properties().destination());
    }
}
