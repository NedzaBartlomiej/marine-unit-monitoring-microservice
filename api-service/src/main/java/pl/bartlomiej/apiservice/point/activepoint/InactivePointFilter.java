package pl.bartlomiej.apiservice.point.activepoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import pl.bartlomiej.apiservice.common.error.apiexceptions.MmsiConflictException;
import pl.bartlomiej.apiservice.point.activepoint.service.ActivePointService;
import pl.bartlomiej.apiservice.shiptracking.service.ShipTrackService;
import pl.bartlomiej.apiservice.user.nested.trackedship.service.TrackedShipService;
import reactor.core.publisher.Mono;

import java.util.List;

import static reactor.core.publisher.Mono.empty;
import static reactor.core.publisher.Mono.error;

@Component
public class InactivePointFilter {

    private static final Logger log = LoggerFactory.getLogger(InactivePointFilter.class);
    private final ActivePointService activePointService;
    private final ShipTrackService shipTrackService;
    private final TrackedShipService trackedShipService;

    public InactivePointFilter(
            @Qualifier("activePointServiceImpl") ActivePointService activePointService,
            ShipTrackService shipTrackService,
            TrackedShipService trackedShipService) {
        this.activePointService = activePointService;
        this.shipTrackService = shipTrackService;
        this.trackedShipService = trackedShipService;
    }

    public Mono<Void> filter(List<String> activeMmsis) {
        return activePointService.getMmsis()
                .flatMap(actualMmsis -> {

                    if (activeMmsis.isEmpty()) {
                        return error(new MmsiConflictException("Active mmsis is empty."));
                    }

                    // exclude matching mmsis and detailing inactive mmsis
                    List<String> inactiveMmsis = actualMmsis.stream()
                            .filter(actualMmsi -> !activeMmsis.contains(actualMmsi))
                            .toList();

                    if (inactiveMmsis.isEmpty()) {
                        log.info("All points are active.");
                    } else {
                        inactiveMmsis
                                .forEach(mmsi -> {
                                    log.info("Removing inactive point - {}", mmsi);
                                    activePointService.removeActivePoint(mmsi)
                                            .doOnError(e -> log.warn("Active points - {}", e.getMessage()))
                                            .subscribe();
                                    trackedShipService.removeTrackedShip(mmsi)
                                            .doOnError(e -> log.warn("Tracked ships - {}", e.getMessage()))
                                            .subscribe();
                                    shipTrackService.clearShipHistory(mmsi)
                                            .doOnError(e -> log.warn("Ship track history - {}", e.getMessage()))
                                            .subscribe();
                                });
                    }

                    return empty();
                })
                .doOnError(err -> log.warn("Something processIssue when filtering - {}", err.getMessage()))
                .then();
    }
}
