package pl.bartlomiej.apiservice.shippoint;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiej.apiservice.common.helper.CacheControlHelper;
import pl.bartlomiej.mumcommons.core.model.response.ResponseModel;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/v1/shipPoints")
public class ShipPointController {

    private final ShipMapManager shipMapManager;
    private final long shipPointMapRefreshment;

    public ShipPointController(ShipMapManager shipMapManager,
                               @Value("${project-properties.scheduling-delays.in-ms.ship-point-map-refreshment}") long shipPointMapRefreshment) {
        this.shipMapManager = shipMapManager;
        this.shipPointMapRefreshment = shipPointMapRefreshment;
    }

    @GetMapping
    public ResponseEntity<ResponseModel<List<ShipPoint>>> getShipPoints() {
        Duration timeElapsedFromLastMapRefreshment = Duration.between(shipMapManager.lastRefreshed(), LocalDateTime.now());
        Duration maxAge = Duration.ofMillis(this.shipPointMapRefreshment).minus(timeElapsedFromLastMapRefreshment);
        return ResponseEntity.status(OK)
                .cacheControl(CacheControl
                        .maxAge(CacheControlHelper.getSafeMaxAge(
                                        maxAge,
                                        Duration.ofSeconds(15)
                                )
                        )
                        .mustRevalidate()
                        .cachePublic()
                )
                .body(new ResponseModel.Builder<List<ShipPoint>>(OK, true)
                        .body(shipMapManager.getActiveShipPoints())
                        .build()
                );
    }
}