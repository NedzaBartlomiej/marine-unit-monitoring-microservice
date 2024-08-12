package pl.bartlomiej.apiservice.point.activepoint.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import pl.bartlomiej.apiservice.ais.service.AisService;
import pl.bartlomiej.apiservice.common.error.apiexceptions.MmsiConflictException;
import pl.bartlomiej.apiservice.point.activepoint.ActivePoint;
import pl.bartlomiej.apiservice.point.activepoint.repository.MongoActivePointRepository;
import reactor.core.publisher.Mono;

import java.util.List;

import static pl.bartlomiej.apiservice.common.error.apiexceptions.MmsiConflictException.Message.INVALID_SHIP;
import static reactor.core.publisher.Mono.*;

@Service
public class ActivePointServiceImpl implements ActivePointService {

    private static final Logger log = LoggerFactory.getLogger(ActivePointServiceImpl.class);
    private final MongoActivePointRepository activePointRepository;
    private final AisService aisService;

    public ActivePointServiceImpl(MongoActivePointRepository activePointRepository, AisService aisService) {
        this.activePointRepository = activePointRepository;
        this.aisService = aisService;
    }

    @Override
    public Mono<List<String>> getMmsis() {
        return activePointRepository.findAll()
                .switchIfEmpty(error(new MmsiConflictException("No active points found.")))
                .map(ActivePoint::getMmsi)
                .collectList();
    }

    @Override
    public Mono<Void> removeActivePoint(String mmsi) {
        return this.isPointActive(mmsi)
                .flatMap(exists -> activePointRepository.deleteById(mmsi));
    }

    @Override
    public Mono<Void> addActivePoint(ActivePoint activePoint) {
        return activePointRepository.existsById(activePoint.getMmsi())
                .flatMap(exists -> {
                    if (exists) {
                        log.warn("Point already exists.");
                        return empty();
                    } else {
                        return activePointRepository.save(activePoint).then();
                    }
                });
    }

    @EventListener(ApplicationReadyEvent.class)
    private Mono<Void> updateAfterAppStart() {
        log.info("Adding new active points after application start if exists.");
        return from(aisService.fetchLatestShips()
                .flatMap(aisShip -> this.addActivePoint(
                                new ActivePoint(
                                        aisShip.properties().mmsi().toString(),
                                        aisShip.properties().name()
                                )
                        )
                )
        );
    }

    @Override
    public Mono<Boolean> isPointActive(String mmsi) {
        return activePointRepository.existsById(mmsi)
                .flatMap(exists -> exists
                        ? just(true)
                        : error(new MmsiConflictException(INVALID_SHIP.message)));
    }

    @Override
    public Mono<String> getName(String mmsi) {
        return activePointRepository.findById(mmsi)
                .map(ActivePoint::getName)
                .switchIfEmpty(error(new MmsiConflictException(INVALID_SHIP.message)));
    }
}
