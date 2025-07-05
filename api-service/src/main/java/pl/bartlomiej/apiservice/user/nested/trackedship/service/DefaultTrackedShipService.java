package pl.bartlomiej.apiservice.user.nested.trackedship.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.bartlomiej.apiservice.point.activepoint.service.ActivePointService;
import pl.bartlomiej.apiservice.user.nested.trackedship.TrackedShip;
import pl.bartlomiej.apiservice.user.repository.CustomUserRepository;
import pl.bartlomiej.apiservice.user.service.UserService;

import java.util.List;

@Service
public class DefaultTrackedShipService implements TrackedShipService {

    private static final Logger log = LoggerFactory.getLogger(DefaultTrackedShipService.class);
    private final UserService userService;
    private final CustomUserRepository customUserRepository;
    private final ActivePointService activePointService;

    public DefaultTrackedShipService(
            UserService userService,
            CustomUserRepository customUserRepository,
            ActivePointService activePointService) {
        this.userService = userService;
        this.customUserRepository = customUserRepository;
        this.activePointService = activePointService;
    }

    @Override
    public List<TrackedShip> getTrackedShips(String id) {
        // todo: (part of: ActivePoints Redis State update): return TrackedShipResponseDto where the `isActive` field will be added
        return customUserRepository.getTrackedShips(id);
    }

    @Override
    public TrackedShip addTrackedShip(String id, String mmsi) {
        // wygodniejszy endpoint przez to ze nie podaje sie name, oraz przy okazji moze zostac sprawdzone czy ktos nie podal blednego/nieaktywnego statku, ktorego po prostu nie dodajemy, (jak usunal sie w miedzyczasie tego requestu no to trudno, user zacznie sobie go sledzic znowu jak bedzie aktywny na mapie - wiec nic nie traci bo i tak bylby nieaktywny, a niwelujemy potencjalne bugi zwiazane z pobieraniem historii dla takeigp punktu, no i oczywiscie obslugujemy przypadek blednego mmsi)
        // todo: (part of: ActivePoints Redis State update): create new TrackedShip(mmsi, name-->from Redis Points Map Representation) - handle if ship is not active on the map (throw InvalidMmsiException(INVALID_SHIP))
        customUserRepository.pushTrackedShip(id, trackedShip);
        return trackedShip;
    }

    @Override
    public void removeTrackedShip(String id, String mmsi) {
        customUserRepository.pullTrackedShip(id, mmsi);
    }
}
