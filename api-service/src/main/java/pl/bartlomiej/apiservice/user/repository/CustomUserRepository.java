package pl.bartlomiej.apiservice.user.repository;

import pl.bartlomiej.apiservice.user.nested.trackedship.TrackedShip;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomUserRepository {

    Mono<TrackedShip> pushTrackedShip(String id, TrackedShip trackedShip);

    Mono<Void> pullTrackedShip(String id, String mmsi);

    Mono<Void> pullTrackedShip(String mmsi);

    Flux<TrackedShip> getTrackedShips(String id);

    Flux<TrackedShip> getTrackedShips();

    Mono<Void> pushTrustedIpAddress(String id, String ipAddress);
}
