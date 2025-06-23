package pl.bartlomiej.apiservice.shiptracking.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.bartlomiej.apiservice.ais.service.AisService;
import pl.bartlomiej.apiservice.common.exception.apiexception.RecordNotFoundException;
import pl.bartlomiej.apiservice.common.helper.DateRangeHelper;
import pl.bartlomiej.apiservice.point.activepoint.service.ActivePointService;
import pl.bartlomiej.apiservice.shiptracking.ShipTrack;
import pl.bartlomiej.apiservice.shiptracking.ShipTrackConstants;
import pl.bartlomiej.apiservice.shiptracking.repository.CustomShipTrackRepository;
import pl.bartlomiej.apiservice.shiptracking.repository.MongoShipTrackRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
class DefaultShipTrackService implements ShipTrackService {

    private static final Logger log = LoggerFactory.getLogger(DefaultShipTrackService.class);
    private final AisService aisService;
    private final MongoShipTrackRepository mongoShipTrackRepository;
    private final CustomShipTrackRepository customShipTrackRepository;
    private final ActivePointService activePointService;

    public DefaultShipTrackService(
            AisService aisService,
            MongoShipTrackRepository mongoShipTrackRepository,
            CustomShipTrackRepository customShipTrackRepository,
            ActivePointService activePointService) {
        this.aisService = aisService;
        this.mongoShipTrackRepository = mongoShipTrackRepository;
        this.customShipTrackRepository = customShipTrackRepository;
        this.activePointService = activePointService;
    }


    // TRACK HISTORY - operations
    @Scheduled(initialDelay = 0, fixedDelayString = "${project-properties.scheduling-delays.in-ms.ship-tracking.saving}")
    public void saveTracksForTrackedShips() {
        this.getShipTracks()
                .forEach(shipTrack -> {
                    try {
                        this.saveNoStationaryTrack(shipTrack);
                    } catch (Exception e) {
                        log.error("Error saving ship track: {}", shipTrack.getMmsi(), e);
                    }
                });
        log.info("Successfully saved tracked ships coordinates.");
    }

    private void saveNoStationaryTrack(ShipTrack shipTrack) {
        ShipTrack latest = customShipTrackRepository.getLatest(shipTrack.getMmsi());
        if ((latest.getX().equals(shipTrack.getX()) && latest.getY().equals(shipTrack.getY()))) {
            log.warn("Ship hasn't changed its position - saving canceled");
        } else {
            mongoShipTrackRepository.save(shipTrack);
        }
    }

    @Override
    public List<ShipTrack> getShipTracks(List<String> mmsis, LocalDateTime from, LocalDateTime to) {
        DateRangeHelper validDateRange = new DateRangeHelper(from, to);
        return this.customShipTrackRepository
                .findByMmsiInAndReadingTimeBetween(mmsis,
                        validDateRange.from(), validDateRange.to());
    }

    public void clearShipHistory(String mmsi) {
        if (mongoShipTrackRepository.existsById(mmsi)) {
            mongoShipTrackRepository.deleteAllByMmsi(mmsi);
        } else {
            throw new RecordNotFoundException("ShipTrack by mmsi not found.");
        }
    }

    // GET SHIP TRACKS TO SAVE - operations
    private Stream<ShipTrack> getShipTracks() {
        return aisService.fetchShipsByMmsis(this.getShipMmsisToTrack())
                .stream()
                .map(this::mapToShipTrack);
    }

    private List<String> getShipMmsisToTrack() {
        return activePointService.getMmsis();
    }

    private ShipTrack mapToShipTrack(JsonNode ship) {

        final String LONGITUDE = "longitude";
        final String LATITUDE = "latitude";

        return new ShipTrack(
                ship.get(ShipTrackConstants.MMSI).asText(),
                ship.get(LONGITUDE).asDouble(),
                ship.get(LATITUDE).asDouble()
        );
    }

}