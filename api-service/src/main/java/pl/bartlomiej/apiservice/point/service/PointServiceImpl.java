package pl.bartlomiej.apiservice.point.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pl.bartlomiej.apiservice.ais.service.AisService;
import pl.bartlomiej.apiservice.ais.AisShip;
import pl.bartlomiej.apiservice.common.error.apiexceptions.NoContentException;
import pl.bartlomiej.apiservice.geocoding.service.GeocodeService;
import pl.bartlomiej.apiservice.geocoding.Position;
import pl.bartlomiej.apiservice.point.Point;
import reactor.core.publisher.Flux;

import java.util.Objects;

import static pl.bartlomiej.apiservice.ais.nested.Geometry.X_COORDINATE_INDEX;
import static pl.bartlomiej.apiservice.ais.nested.Geometry.Y_COORDINATE_INDEX;
import static pl.bartlomiej.apiservice.common.config.RedisCacheConfig.POINTS_CACHE_NAME;
import static reactor.core.publisher.Flux.error;

@Service
public class PointServiceImpl implements PointService {

    public static final String UNKNOWN_NOT_REPORTED = "UNKNOWN (NOT REPORTED)";
    private final AisService aisService;
    private final GeocodeService geocodeService;

    public PointServiceImpl(AisService aisService, GeocodeService geocodeService) {
        this.aisService = aisService;
        this.geocodeService = geocodeService;
    }

    @Cacheable(cacheNames = POINTS_CACHE_NAME)
    @Override
    public Flux<Point> getPoints() {
        return aisService.fetchLatestShips()
                .switchIfEmpty(error(NoContentException::new))
                .flatMap(this::mapToPoint)
                .cache();
    }

    private Flux<Point> mapToPoint(AisShip aisShip) {

        String mayNullName = Objects.requireNonNullElse(aisShip.properties().name(), UNKNOWN_NOT_REPORTED);
        String mayNullDestination = Objects.requireNonNullElse(aisShip.properties().destination(), UNKNOWN_NOT_REPORTED);

        return this.getShipDestinationCoordinates(aisShip)
                .map(position ->
                        new Point(
                                aisShip.properties().mmsi().toString(),
                                mayNullName,
                                aisShip.geometry().coordinates().get(X_COORDINATE_INDEX),
                                aisShip.geometry().coordinates().get(Y_COORDINATE_INDEX),
                                mayNullDestination,
                                position.x(),
                                position.y()
                        )
                );
    }

    private Flux<Position> getShipDestinationCoordinates(AisShip aisShip) {
        return geocodeService.getAddressCoordinates(aisShip.properties().destination());
    }
}
