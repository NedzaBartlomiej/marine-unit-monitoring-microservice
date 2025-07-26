package pl.bartlomiej.apiservice.shippoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.bartlomiej.apiservice.aisapi.AisShip;
import pl.bartlomiej.apiservice.aisapi.service.AisService;
import pl.bartlomiej.apiservice.common.helper.Position;
import pl.bartlomiej.apiservice.geocoding.GeocodeService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static pl.bartlomiej.apiservice.aisapi.nested.Geometry.X_COORDINATE_INDEX;
import static pl.bartlomiej.apiservice.aisapi.nested.Geometry.Y_COORDINATE_INDEX;

@Slf4j
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
    public Optional<List<ShipPoint>> getLatestShipPoints() {
        log.debug("Obtaining latest ShipPoints.");
        return aisService.fetchLatestShips()
                .map(aisShips ->
                        aisShips.stream()
                                .map(this::mapToPoint)
                                .toList()
                );
    }

    private ShipPoint mapToPoint(AisShip aisShip) {
        String nameOrUnknown = Objects.requireNonNullElse(aisShip.properties().name(), UNKNOWN_NOT_REPORTED);
        String destinationOrUnknown = Objects.requireNonNullElse(aisShip.properties().destination(), UNKNOWN_NOT_REPORTED);

        Position destinationPosition = this.geocodeService.getAddressCoordinates(
                aisShip.properties().destination()
        ).orElse(null);

        return new ShipPoint(
                aisShip.properties().mmsi().toString(),
                nameOrUnknown,
                aisShip.geometry().coordinates().get(X_COORDINATE_INDEX),
                aisShip.geometry().coordinates().get(Y_COORDINATE_INDEX),
                destinationOrUnknown,
                destinationPosition
        );
    }
}
