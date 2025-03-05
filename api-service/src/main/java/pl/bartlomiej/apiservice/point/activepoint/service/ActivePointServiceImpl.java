package pl.bartlomiej.apiservice.point.activepoint.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import pl.bartlomiej.apiservice.ais.AisShip;
import pl.bartlomiej.apiservice.ais.service.AisService;
import pl.bartlomiej.apiservice.common.exception.apiexception.MmsiConflictException;
import pl.bartlomiej.apiservice.point.activepoint.ActivePoint;
import pl.bartlomiej.apiservice.point.activepoint.repository.MongoActivePointRepository;

import java.util.List;

import static pl.bartlomiej.apiservice.common.exception.apiexception.MmsiConflictException.Message.INVALID_SHIP;

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
    public List<String> getMmsis() {
        List<ActivePoint> activePoints = activePointRepository.findAll();
        if (activePoints.isEmpty()) throw new MmsiConflictException("No active points found.");
        return activePoints.stream()
                .map(ActivePoint::getMmsi)
                .toList();
    }

    @Override
    public void removeActivePoint(String mmsi) {
        if (!this.isPointActive(mmsi)) throw new MmsiConflictException(INVALID_SHIP.message);
        activePointRepository.deleteById(mmsi);
    }

    @Override
    public void addActivePoint(ActivePoint activePoint) {
        boolean exists = activePointRepository.existsById(activePoint.getMmsi());
        if (!exists) log.warn("Point already exists.");
        else activePointRepository.save(activePoint);
    }

    @Override
    public boolean isPointActive(String mmsi) {
        return activePointRepository.existsById(mmsi);
    }

    @Override
    public String getName(String mmsi) {
        return activePointRepository.findById(mmsi)
                .map(ActivePoint::getName)
                .orElseThrow(() -> new MmsiConflictException(INVALID_SHIP.message));
    }


    @EventListener(ApplicationReadyEvent.class)
    private void updateAfterAppStarts() {
        log.info("Adding new active points after application start if exists.");
        List<AisShip> aisShips = aisService.fetchLatestShips();
        if (aisShips.isEmpty()) return;
        aisShips.forEach(aisShip ->
                this.addActivePoint(
                        new ActivePoint(
                                aisShip.properties().mmsi().toString(),
                                aisShip.properties().name()
                        )
                )
        );
    }
}
