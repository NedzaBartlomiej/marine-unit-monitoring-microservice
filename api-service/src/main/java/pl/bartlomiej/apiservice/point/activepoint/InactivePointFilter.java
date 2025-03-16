package pl.bartlomiej.apiservice.point.activepoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import pl.bartlomiej.apiservice.common.exception.apiexception.MmsiConflictException;
import pl.bartlomiej.apiservice.point.activepoint.service.ActivePointService;
import pl.bartlomiej.apiservice.shiptracking.service.ShipTrackService;
import pl.bartlomiej.apiservice.user.nested.trackedship.service.TrackedShipService;

import java.util.List;

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

    /**
     * Function that removes Inactive Points basing on its argument. Deletes all connections and data of this point.
     * @param activePointsMmsis A list of currently active points
     */
    public void filter(List<String> activePointsMmsis) {
        if (activePointsMmsis.isEmpty()) {
            throw new MmsiConflictException("Filtered ActivePointsMmsis cannot be empty.");
        }

        List<String> currStoredActivePointsMmsis = activePointService.getMmsis();
        List<String> inactiveMmsis = currStoredActivePointsMmsis.stream()
                .filter(currMmsi -> !activePointsMmsis.contains(currMmsi))
                .toList();
        if (inactiveMmsis.isEmpty()) {
            log.info("All points are active. End of the filtering.");
            return;
        }

        log.info("There are some inactive points, permanently removing them");
        inactiveMmsis.forEach(inactiveMmsi -> {
            log.info("Removing inactive point: {}.", inactiveMmsi);
            log.info("From the ActivePoint list.");
            activePointService.removeActivePoint(inactiveMmsi);
            log.info("From the ShipTracking list.");
            trackedShipService.removeTrackedShip(inactiveMmsi);
            log.info("From the ShipTracking history.");
            shipTrackService.clearShipHistory(inactiveMmsi);
        });
    }
}
