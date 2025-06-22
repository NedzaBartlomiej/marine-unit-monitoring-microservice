package pl.bartlomiej.apiservice.shiptracking;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.bartlomiej.apiservice.common.seeemission.SseEmissionManager;
import pl.bartlomiej.apiservice.shiptracking.service.ShipTrackService;
import pl.bartlomiej.mumcommons.core.model.response.ResponseModel;
import reactor.core.publisher.Flux;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/v1/ship-tracks")
public class ShipTrackController {

    private final ShipTrackService shipTrackService;
    private final SseEmissionManager sseEmissionManager;

    public ShipTrackController(ShipTrackService shipTrackService,
                               @Qualifier("shipTrackInMemorySseEmissionManager") SseEmissionManager sseEmissionManager) {
        this.shipTrackService = shipTrackService;
        this.sseEmissionManager = sseEmissionManager;
    }

    @PreAuthorize("hasRole(T(pl.bartlomiej.apiservice.user.domain.UserKeycloakRole).API_PREMIUM_USER.getRole())")
    @GetMapping
    public Flux<ServerSentEvent<ResponseModel<ShipTrack>>> getShipTrackHistory(
            Principal principal,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to) {

        return userService.getEntity(principal.getName())
                .flatMapMany(user -> shipTrackService.getShipTrackHistory(user.getId(), from, to)
                        .map(response ->
                                ServerSentEvent.<ResponseModel<ShipTrack>>builder()
                                        .id(response.getMmsi())
                                        .event("NEW_SHIP_TRACK_EVENT")
                                        .data(new ResponseModel.Builder<ShipTrack>(OK, true)
                                                .body(response)
                                                .build()
                                        )
                                        .build()
                        )
                );
    }

    // IMPERATIVE

    @PreAuthorize("hasRole(T(pl.bartlomiej.apiservice.user.domain.UserKeycloakRole).API_PREMIUM_USER.getRole())")
    @GetMapping
    public List<ShipTrack> getShipTracks(@RequestParam(required = false) LocalDateTime from,
                                         @RequestParam(required = false) LocalDateTime to,
                                         Principal principal) {

    }

    @PreAuthorize("hasRole(T(pl.bartlomiej.apiservice.user.domain.UserKeycloakRole).API_PREMIUM_USER.getRole())")
    @GetMapping("/stream")
    public SseEmitter getShipTrackStream(@RequestHeader("x-api-key") String xApiKey) {
        return this.sseEmissionManager.getOrCreateEmitter(xApiKey);
    }

    // disconnect from stream;
}