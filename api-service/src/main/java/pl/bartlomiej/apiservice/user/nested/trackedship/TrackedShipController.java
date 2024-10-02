package pl.bartlomiej.apiservice.user.nested.trackedship;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiej.apiservice.common.helper.ResponseModel;
import pl.bartlomiej.apiservice.user.nested.trackedship.service.TrackedShipService;
import pl.bartlomiej.apiservice.user.service.UserService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.ok;
import static pl.bartlomiej.apiservice.common.util.ControllerResponseUtil.buildResponse;
import static pl.bartlomiej.apiservice.common.util.ControllerResponseUtil.buildResponseModel;
import static reactor.core.publisher.Mono.just;

@RestController
@RequestMapping("/v1/tracked-ships")
public class TrackedShipController {

    private final TrackedShipService trackedShipService;
    private final UserService userService;

    public TrackedShipController(TrackedShipService trackedShipService, UserService userService) {
        this.trackedShipService = trackedShipService;
        this.userService = userService;
    }

    @PreAuthorize("hasAnyRole(" +
            "T(pl.bartlomiej.apiservice.user.UserKeycloakRole).API_PREMIUM_USER.name())," +
            "'ADMIN'," +
            "'SUPERADMIN'" +
            ")"
    )
    @GetMapping // todo pageable
    public ResponseEntity<Flux<ResponseModel<TrackedShip>>> getTrackedShips(Principal principal) {
        return ok(userService.getUser(principal.getName())
                .flatMapMany(user -> trackedShipService.getTrackedShips(user.getId())
                        .map(trackedShip ->
                                buildResponseModel(
                                        null,
                                        OK,
                                        trackedShip,
                                        "trackedShip"
                                )
                        )
                )
        );
    }

    @PreAuthorize("hasAnyRole(" +
            "T(pl.bartlomiej.apiservice.user.UserKeycloakRole).API_PREMIUM_USER.name())," +
            "'ADMIN'," +
            "'SUPERADMIN'" +
            ")"
    )
    @PostMapping("/{mmsi}")
    public Mono<ResponseEntity<ResponseModel<TrackedShip>>> addTrackedShip(Principal principal, @PathVariable String mmsi) {
        return userService.getUser(principal.getName())
                .flatMap(user -> trackedShipService.addTrackedShip(user.getId(), mmsi)
                        .map(trackedShip ->
                                buildResponse(
                                        CREATED,
                                        buildResponseModel(
                                                "ADDED_TO_LIST",
                                                CREATED,
                                                trackedShip,
                                                "trackedShip"
                                        )
                                )
                        )
                );
    }

    @PreAuthorize("hasAnyRole(" +
            "T(pl.bartlomiej.apiservice.user.UserKeycloakRole).API_PREMIUM_USER.name())," +
            "'ADMIN'," +
            "'SUPERADMIN'" +
            ")"
    )
    @DeleteMapping("/{mmsi}")
    public Mono<ResponseEntity<ResponseModel<Void>>> removeTrackedShip(Principal principal, @PathVariable String mmsi) {

        return userService.getUser(principal.getName())
                .flatMap(user -> trackedShipService.removeTrackedShip(user.getId(), mmsi)
                        .then(just(
                                buildResponse(
                                        OK,
                                        buildResponseModel(
                                                "REMOVED_FROM_LIST",
                                                OK,
                                                null,
                                                null
                                        )
                                )
                        ))
                );
    }
}
