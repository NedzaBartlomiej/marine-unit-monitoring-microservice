package pl.bartlomiej.apiservice.shiptracking.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.bartlomiej.apiservice.aisapi.service.AisService;
import pl.bartlomiej.apiservice.common.helper.DateRangeHelper;
import pl.bartlomiej.apiservice.shippoint.ShipMapManager;
import pl.bartlomiej.apiservice.shiptracking.ShipTrack;
import pl.bartlomiej.apiservice.shiptracking.ShipTrackConstants;
import pl.bartlomiej.apiservice.shiptracking.repository.CustomShipTrackRepository;
import pl.bartlomiej.apiservice.shiptracking.repository.MongoShipTrackRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
class DefaultShipTrackService implements ShipTrackService {

    private static final Logger log = LoggerFactory.getLogger(DefaultShipTrackService.class);
    private final AisService aisService;
    private final MongoShipTrackRepository mongoShipTrackRepository;
    private final CustomShipTrackRepository customShipTrackRepository;
    private final ShipMapManager shipMapManager;

    public DefaultShipTrackService(
            AisService aisService,
            MongoShipTrackRepository mongoShipTrackRepository,
            CustomShipTrackRepository customShipTrackRepository,
            ShipMapManager shipMapManager) {
        this.aisService = aisService;
        this.mongoShipTrackRepository = mongoShipTrackRepository;
        this.customShipTrackRepository = customShipTrackRepository;
        this.shipMapManager = shipMapManager;
    }

    // TRACK HISTORY - operations
    @Scheduled(initialDelay = 20000, fixedDelayString = "${project-properties.scheduling-delays.in-ms.ship-tracking.saving}")
    public void saveShipTracks() {
        log.info("Starting saving ShipTracks process.");
        try {
            this.saveNoStationaryShipTracks(this.getActualShipTracks());
            log.info("Saving ShipTracks succeed.");
        } catch (Exception e) {
            log.error("Something went wrong during saving ShipTracks.", e);
        }
    }

    private void saveNoStationaryShipTracks(List<ShipTrack> shipTracksToSave) {
        Set<String> shipTrackToSaveMmsis = shipTracksToSave.stream()
                .map(ShipTrack::getMmsi)
                .collect(Collectors.toSet());
        Map<String, ShipTrack> latestShipTracksForMmsis = this.customShipTrackRepository.getLatestShipTracksForMmsis(shipTrackToSaveMmsis);

        List<ShipTrack> noStationaryShipTracks = shipTracksToSave.stream()
                .filter(shipTrackToSave -> {
                    ShipTrack latestShipTrackForMmsi = latestShipTracksForMmsis.get(shipTrackToSave.getMmsi());
                    return latestShipTrackForMmsi == null ||
                            !(latestShipTrackForMmsi.getX().equals(shipTrackToSave.getX())
                                    && latestShipTrackForMmsi.getY().equals(shipTrackToSave.getY())
                            );
                }).toList();
        mongoShipTrackRepository.saveAll(noStationaryShipTracks);
    }

    @Override
    public List<ShipTrack> getShipTracks(List<String> mmsis, LocalDateTime from, LocalDateTime to) {
        DateRangeHelper validDateRange = new DateRangeHelper(from, to);
        return this.customShipTrackRepository
                .findByMmsiInAndReadingTimeBetween(mmsis,
                        validDateRange.from(), validDateRange.to());
    }

    // GET SHIP TRACKS TO SAVE - operations
    private List<ShipTrack> getActualShipTracks() {
        List<String> activeShipMmsis = shipMapManager.getActiveShipMmsis();
        if (activeShipMmsis.isEmpty()) activeShipMmsis = List.of("0");
        // todo: refactor^^^ - just don't make a call to api
        return aisService.fetchShipsByMmsis(activeShipMmsis)
                .stream()
                .map(this::mapToShipTrack)
                .toList();
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