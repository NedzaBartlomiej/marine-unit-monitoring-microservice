package pl.bartlomiej.apiservice.shiptracking.repository;

import pl.bartlomiej.apiservice.shiptracking.ShipTrack;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomShipTrackRepository {
    Flux<ShipTrack> findByMmsiInAndReadingTimeBetween(List<String> mmsis, LocalDateTime from, LocalDateTime to);

    Mono<ShipTrack> getLatest(String mmsi);
}
