package pl.bartlomiej.apiservice.shiptracking;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.bartlomiej.apiservice.common.sseemission.emissionmanager.SseEmissionManager;
import pl.bartlomiej.apiservice.shiptracking.service.ShipTrackService;
import pl.bartlomiej.apiservice.user.nested.trackedship.TrackedShip;
import pl.bartlomiej.apiservice.user.nested.trackedship.service.TrackedShipService;
import pl.bartlomiej.mumcommons.core.model.response.ResponseModel;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/ship-tracks")
public class ShipTrackController {

    private final ShipTrackService shipTrackService;
    private final TrackedShipService trackedShipService;
    private final SseEmissionManager sseEmissionManager;

    public ShipTrackController(ShipTrackService shipTrackService, TrackedShipService trackedShipService,
                               @Qualifier("shipTrackInMemorySseEmissionManager") SseEmissionManager sseEmissionManager) {
        this.shipTrackService = shipTrackService;
        this.trackedShipService = trackedShipService;
        this.sseEmissionManager = sseEmissionManager;
    }

    @PreAuthorize("hasRole(T(pl.bartlomiej.apiservice.user.domain.UserKeycloakRole).API_PREMIUM_USER.getRole())")
    @GetMapping("/me")
    public ResponseEntity<ResponseModel<List<ShipTrack>>> getShipTracks(@RequestParam(required = false) LocalDateTime from,
                                                                        @RequestParam(required = false) LocalDateTime to,
                                                                        Principal principal) {
        Set<String> principalTrackedShipsMmsis = this.trackedShipService.getTrackedShips(principal.getName()).stream()
                .map(TrackedShip::mmsi)
                .collect(Collectors.toUnmodifiableSet());
        List<ShipTrack> shipTracks = this.shipTrackService.getShipTracks(principalTrackedShipsMmsis, from, to);
        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl
                        .maxAge(Duration.ofDays(7))
                        .cachePublic()
                        .immutable()
                )
                .body(new ResponseModel.Builder<List<ShipTrack>>(HttpStatus.OK, true)
                        .body(shipTracks)
                        .build()
                );
    }

    @PreAuthorize("hasRole(T(pl.bartlomiej.apiservice.user.domain.UserKeycloakRole).API_PREMIUM_USER.getRole())")
    @GetMapping
    public ResponseEntity<ResponseModel<List<ShipTrack>>> getShipTracks(@RequestParam(required = false) LocalDateTime from,
                                                                        @RequestParam(required = false) LocalDateTime to,
                                                                        @RequestBody Set<String> mmsis) {
        List<ShipTrack> shipTracks = this.shipTrackService.getShipTracks(mmsis, from, to);
        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl
                        .maxAge(Duration.ofDays(7))
                        .cachePublic()
                        .immutable()
                )
                .body(new ResponseModel.Builder<List<ShipTrack>>(HttpStatus.OK, true)
                        .body(shipTracks)
                        .build()
                );
    }

    @PreAuthorize("hasRole(T(pl.bartlomiej.apiservice.user.domain.UserKeycloakRole).API_PREMIUM_USER.getRole())")
    @GetMapping("/stream")
    public SseEmitter getShipTrackStream(Principal principal) {
        return this.sseEmissionManager.getOrCreateEmitter(principal.getName());
    }
}