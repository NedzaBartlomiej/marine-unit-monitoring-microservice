package pl.bartlomiej.apiservice.user.nested.trackedship.service;

import pl.bartlomiej.apiservice.user.nested.trackedship.TrackedShip;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TrackedShipService {
    List<TrackedShip> getTrackedShips(String id);

    Mono<TrackedShip> addTrackedShip(String id, String mmsi);

    Mono<Void> removeTrackedShip(String id, String mmsi);

    Mono<Void> removeTrackedShip(String mmsi);
}
