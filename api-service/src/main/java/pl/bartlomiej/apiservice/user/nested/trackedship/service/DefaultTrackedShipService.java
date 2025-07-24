package pl.bartlomiej.apiservice.user.nested.trackedship.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.bartlomiej.apiservice.common.exception.apiexception.MmsiConflictException;
import pl.bartlomiej.apiservice.shippoint.ShipMapManager;
import pl.bartlomiej.apiservice.user.nested.trackedship.TrackedShip;
import pl.bartlomiej.apiservice.user.nested.trackedship.TrackedShipResponseDto;
import pl.bartlomiej.apiservice.user.repository.CustomUserRepository;

import java.util.List;

@Service
public class DefaultTrackedShipService implements TrackedShipService {

    private static final Logger log = LoggerFactory.getLogger(DefaultTrackedShipService.class);
    private final CustomUserRepository customUserRepository;
    private final ShipMapManager shipMapManager;

    public DefaultTrackedShipService(CustomUserRepository customUserRepository,
                                     ShipMapManager shipMapManager) {
        this.customUserRepository = customUserRepository;
        this.shipMapManager = shipMapManager;
    }

    @Override
    public List<TrackedShipResponseDto> getTrackedShipsResponse(String id) {
        log.info("Returning tracked ship DTOs with active status for user with id='{}'.", id);
        return customUserRepository.getTrackedShips(id).stream()
                .map(trackedShip -> {
                    boolean shipPointActive = shipMapManager.isShipPointActive(trackedShip.mmsi());
                    return new TrackedShipResponseDto(trackedShip.mmsi(), trackedShip.name(), shipPointActive);
                }).toList();
    }

    @Override
    public List<TrackedShip> getTrackedShips(String id) {
        log.info("Returning raw tracked ships from repository for user with id='{}'.", id);
        return customUserRepository.getTrackedShips(id);
    }

    @Override
    public TrackedShip addTrackedShip(String id, String mmsi) {
        log.info("Adding ship with MMSI='{}' to tracking for user with id='{}'.", mmsi, id);
        return shipMapManager.getShipPointName(mmsi)
                .map(shipName -> customUserRepository
                        .pushTrackedShip(id, new TrackedShip(mmsi, shipName))
                ).orElseThrow(() ->
                        new MmsiConflictException(MmsiConflictException.Message.INVALID_SHIP.message)
                );
    }

    @Override
    public void removeTrackedShip(String id, String mmsi) {
        log.info("Removing ship with MMSI='{}' from tracking for user with id='{}'", mmsi, id);
        customUserRepository.pullTrackedShip(id, mmsi);
    }
}
