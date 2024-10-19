package pl.bartlomiej.apiservice.user.nested.trackedship.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.bartlomiej.apiservice.common.error.apiexceptions.MmsiConflictException;
import pl.bartlomiej.apiservice.point.activepoint.service.ActivePointService;
import pl.bartlomiej.apiservice.user.nested.trackedship.TrackedShip;
import pl.bartlomiej.apiservice.user.repository.CustomUserRepository;
import pl.bartlomiej.apiservice.user.service.UserService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static pl.bartlomiej.apiservice.common.error.apiexceptions.MmsiConflictException.Message.INVALID_SHIP;
import static pl.bartlomiej.apiservice.common.error.apiexceptions.MmsiConflictException.Message.SHIP_IS_ALREADY_TRACKED;

@Service
public class TrackedShipServiceImpl implements TrackedShipService {

    private static final Logger log = LoggerFactory.getLogger(TrackedShipServiceImpl.class);
    private final UserService userService;
    private final CustomUserRepository customUserRepository;
    private final ActivePointService activePointService;

    public TrackedShipServiceImpl(
            UserService userService,
            CustomUserRepository customUserRepository,
            ActivePointService activePointService) {
        this.userService = userService;
        this.customUserRepository = customUserRepository;
        this.activePointService = activePointService;
    }


    public Flux<TrackedShip> getTrackedShips(String id) {
        return customUserRepository.getTrackedShips(id);
    }

    @Override
    public Mono<TrackedShip> addTrackedShip(String id, String mmsi) {
        return userService.isUserExists(id)
                .then(activePointService.isPointActive(mmsi))
                .then(this.isShipTrackedMono(id, mmsi, false))
                .then(activePointService.getName(mmsi)
                        .map(name -> new TrackedShip(mmsi, name))
                )
                .flatMap(trackedShip -> customUserRepository.pushTrackedShip(id, trackedShip));
    }

    @Override
    public Mono<Void> removeTrackedShip(String id, String mmsi) {
        return userService.isUserExists(id)
                .then(this.isShipTrackedMono(id, mmsi, true))
                .then(customUserRepository.pullTrackedShip(id, mmsi));
    }

    @Override
    public Mono<Void> removeTrackedShip(String mmsi) {
        return this.isShipTrackedMono(mmsi, true)
                .then(customUserRepository.pullTrackedShip(mmsi));
    }


    private Mono<Void> isShipTrackedMono(String id, String mmsi, boolean shouldNegate) {
        return this.isShipTracked(id, mmsi)
                .flatMap(this.processIsShipTrackedMono(shouldNegate));
    }

    private Mono<Void> isShipTrackedMono(String mmsi, boolean shouldNegate) {
        return this.isShipTracked(mmsi)
                .flatMap(this.processIsShipTrackedMono(shouldNegate));
    }

    private Function<Boolean, Mono<? extends Void>> processIsShipTrackedMono(boolean shouldShipBeTracked) {
        return isTracked -> {
            if (shouldShipBeTracked) {
                if (!isTracked) {
                    return Mono.error(new MmsiConflictException(INVALID_SHIP.message));
                }
            } else {
                if (isTracked) {
                    return Mono.error(new MmsiConflictException(SHIP_IS_ALREADY_TRACKED.message));
                }
            }
            return Mono.empty();
        };
    }

    private Mono<Boolean> isShipTracked(String id, String mmsi) {
        return customUserRepository.getTrackedShips(id)
                .any(trackedShip -> trackedShip.mmsi().equals(mmsi));
    }

    private Mono<Boolean> isShipTracked(String mmsi) {
        return customUserRepository.getTrackedShips()
                .any(trackedShip -> trackedShip.mmsi().equals(mmsi));
    }
}
