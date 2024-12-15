package pl.bartlomiej.apiservice.shiptracking;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiej.apiservice.shiptracking.service.ShipTrackService;
import pl.bartlomiej.apiservice.user.service.UserService;
import pl.bartlomiej.mumcommons.core.model.response.ResponseModel;
import reactor.core.publisher.Flux;

import java.security.Principal;
import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/v1/ship-track-history")
public class ShipTrackController {

    private final ShipTrackService shipTrackService;
    private final UserService userService;

    public ShipTrackController(ShipTrackService shipTrackService, UserService userService) {
        this.shipTrackService = shipTrackService;
        this.userService = userService;
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
}