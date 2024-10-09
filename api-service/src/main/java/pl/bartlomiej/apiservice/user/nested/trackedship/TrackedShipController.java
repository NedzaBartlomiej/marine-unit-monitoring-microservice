package pl.bartlomiej.apiservice.user.nested.trackedship;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiej.apiservice.user.nested.trackedship.service.TrackedShipService;
import pl.bartlomiej.apiservice.user.service.UserService;
import pl.bartlomiej.mummicroservicecommons.model.response.ResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.ok;
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
            "T(pl.bartlomiej.apiservice.user.domain.UserKeycloakRole).API_PREMIUM_USER.getRole())," +
            "T(pl.bartlomiej.apiservice.user.domain.UserKeycloakRole).API_ADMIN.getRole())" +
            ")"
    )
    @GetMapping // todo pageable
    public ResponseEntity<Flux<ResponseModel<TrackedShip>>> getTrackedShips(Principal principal) {
        return ok(userService.getUser(principal.getName())
                .flatMapMany(user -> trackedShipService.getTrackedShips(user.getId())
                        .map(trackedShip -> new ResponseModel.Builder<TrackedShip>(OK, OK.value())
                                .body(trackedShip)
                                .build()
                        )
                )
        );
    }

    @PreAuthorize("hasRole(" +
            "T(pl.bartlomiej.apiservice.user.domain.UserKeycloakRole).API_PREMIUM_USER.getRole())," +
            "T(pl.bartlomiej.apiservice.user.domain.UserKeycloakRole).API_ADMIN.getRole())" +
            ")"
    )
    @PostMapping("/{mmsi}")
    public Mono<ResponseEntity<ResponseModel<TrackedShip>>> addTrackedShip(Principal principal, @PathVariable String mmsi) {
        return userService.getUser(principal.getName())
                .flatMap(user -> trackedShipService.addTrackedShip(user.getId(), mmsi)
                        .map(trackedShip -> ResponseEntity.status(CREATED)
                                .body(new ResponseModel.Builder<TrackedShip>(CREATED, CREATED.value())
                                        .message("ADDED_TO_LIST")
                                        .body(trackedShip)
                                        .build()
                                )
                        )
                );
    }

    @PreAuthorize("hasAnyRole(" +
            "T(pl.bartlomiej.apiservice.user.domain.UserKeycloakRole).API_PREMIUM_USER.getRole())," +
            "T(pl.bartlomiej.apiservice.user.domain.UserKeycloakRole).API_ADMIN.getRole())" +
            ")"
    )
    @DeleteMapping("/{mmsi}")
    public Mono<ResponseEntity<ResponseModel<Void>>> removeTrackedShip(Principal principal, @PathVariable String mmsi) {
        return userService.getUser(principal.getName())
                .flatMap(user -> trackedShipService.removeTrackedShip(user.getId(), mmsi)
                        .then(just(ResponseEntity.status(OK)
                                .body(new ResponseModel.Builder<Void>(OK, OK.value())
                                        .message("REMOVED_FROM_LIST")
                                        .build()
                                )
                        ))
                );
    }
}