package pl.bartlomiej.apiservice.user.nested.trackedship.service;

import pl.bartlomiej.apiservice.user.nested.trackedship.TrackedShip;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TrackedShipService {
    Flux<TrackedShip> getTrackedShips(String id);

    Mono<TrackedShip> addTrackedShip(String id, String mmsi);

    Mono<Void> removeTrackedShip(String id, String mmsi);

    Mono<Void> removeTrackedShip(String mmsi);
}
