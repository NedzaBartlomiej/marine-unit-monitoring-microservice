package pl.bartlomiej.apiservice.user.nested.trackedship;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiej.apiservice.common.helper.CacheControlHelper;
import pl.bartlomiej.apiservice.shippoint.ShipMapManager;
import pl.bartlomiej.apiservice.user.domain.ApiUserEntity;
import pl.bartlomiej.apiservice.user.nested.trackedship.service.TrackedShipService;
import pl.bartlomiej.apiservice.user.service.UserService;
import pl.bartlomiej.mumcommons.core.model.response.ResponseModel;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/v1/tracked-ships")
public class TrackedShipController {

    private final TrackedShipService trackedShipService;
    private final UserService userService;
    private final ShipMapManager shipMapManager;
    private final long shipPointMapRefreshment;

    public TrackedShipController(TrackedShipService trackedShipService,
                                 UserService userService,
                                 ShipMapManager shipMapManager,
                                 @Value("${project-properties.scheduling-delays.in-ms.ship-point-map-refreshment}") long shipPointMapRefreshment) {
        this.trackedShipService = trackedShipService;
        this.userService = userService;
        this.shipMapManager = shipMapManager;
        this.shipPointMapRefreshment = shipPointMapRefreshment;
    }

    @PreAuthorize("hasAnyRole(" +
            "T(pl.bartlomiej.apiservice.user.domain.UserKeycloakRole).API_PREMIUM_USER.getRole())," +
            "T(pl.bartlomiej.apiservice.user.domain.UserKeycloakRole).API_ADMIN.getRole())" +
            ")"
    )
    @GetMapping
    public ResponseEntity<ResponseModel<List<TrackedShipResponseDto>>> getTrackedShips(Principal principal) {
        ApiUserEntity user = userService.getEntity(principal.getName());
        List<TrackedShipResponseDto> trackedShips = trackedShipService.getTrackedShipsResponse(user.getId());
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
                        .cachePrivate()
                )
                .body(new ResponseModel.Builder<List<TrackedShipResponseDto>>(OK, true)
                        .body(trackedShips)
                        .build()
                );
    }

    @PreAuthorize("hasRole(" +
            "T(pl.bartlomiej.apiservice.user.domain.UserKeycloakRole).API_PREMIUM_USER.getRole())," +
            "T(pl.bartlomiej.apiservice.user.domain.UserKeycloakRole).API_ADMIN.getRole())" +
            ")"
    )
    @PostMapping("/{mmsi}")
    public ResponseEntity<ResponseModel<TrackedShip>> addTrackedShip(Principal principal, @PathVariable String mmsi) {
        ApiUserEntity user = userService.getEntity(principal.getName());
        TrackedShip trackedShip = trackedShipService.addTrackedShip(user.getId(), mmsi);
        return ResponseEntity.status(CREATED)
                .body(new ResponseModel.Builder<TrackedShip>(CREATED, true)
                        .message("ADDED_TO_LIST")
                        .body(trackedShip)
                        .build()
                );
    }

    @PreAuthorize("hasAnyRole(" +
            "T(pl.bartlomiej.apiservice.user.domain.UserKeycloakRole).API_PREMIUM_USER.getRole())," +
            "T(pl.bartlomiej.apiservice.user.domain.UserKeycloakRole).API_ADMIN.getRole())" +
            ")"
    )
    @DeleteMapping("/{mmsi}")
    public ResponseEntity<ResponseModel<Void>> removeTrackedShip(Principal principal, @PathVariable String mmsi) {
        ApiUserEntity user = userService.getEntity(principal.getName());
        trackedShipService.removeTrackedShip(user.getId(), mmsi);
        return ResponseEntity.status(OK)
                .body(new ResponseModel.Builder<Void>(OK, true)
                        .message("REMOVED_FROM_LIST")
                        .build()
                );
    }
}