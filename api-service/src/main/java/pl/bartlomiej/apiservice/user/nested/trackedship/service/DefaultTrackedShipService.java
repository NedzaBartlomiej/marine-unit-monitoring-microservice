package pl.bartlomiej.apiservice.user.nested.trackedship.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
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
        return customUserRepository.getTrackedShips(id).stream()
                .map(trackedShip -> {
                    boolean shipPointActive = shipMapManager.isShipPointActive(trackedShip.mmsi());
                    return new TrackedShipResponseDto(trackedShip.mmsi(), trackedShip.name(), shipPointActive);
                }).toList();
    }

    @Override
    public List<TrackedShip> getTrackedShips(String id) {
        return customUserRepository.getTrackedShips(id);
    }

    @Override
    public TrackedShip addTrackedShip(String id, String mmsi) {
        return customUserRepository.pushTrackedShip(id,
                new TrackedShip(mmsi, shipMapManager.getShipPointName(mmsi))
        );
    }

    @Override
    public void removeTrackedShip(String id, String mmsi) {
        customUserRepository.pullTrackedShip(id, mmsi);
    }
}
