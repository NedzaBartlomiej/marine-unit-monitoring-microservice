package pl.bartlomiej.apiservice.shiptracking.service;

import pl.bartlomiej.apiservice.shiptracking.ShipTrack;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface ShipTrackService {
    Flux<ShipTrack> getShipTrackHistory(String userId, LocalDateTime from, LocalDateTime to);

    Mono<Void> clearShipHistory(String mmsi);
}
