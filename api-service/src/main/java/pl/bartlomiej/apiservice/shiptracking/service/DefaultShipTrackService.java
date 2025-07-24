package pl.bartlomiej.apiservice.shiptracking.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.bartlomiej.apiservice.aisapi.AisApiConstants;
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
class DefaultShipTrackService implements ShipTrackService {

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

    @Override
    public List<ShipTrack> getShipTracks(List<String> mmsis, LocalDateTime from, LocalDateTime to) {
        log.info("Returning tracking history for passed mmsis.");
        DateRangeHelper validDateRange = new DateRangeHelper(from, to);
        return this.customShipTrackRepository
                .findByMmsiInAndReadingTimeBetween(mmsis,
                        validDateRange.from(), validDateRange.to());
    }

    @Scheduled(initialDelayString = "${project-properties.scheduling-delays.in-ms.ship-tracking.initialDelay}", fixedDelayString = "${project-properties.scheduling-delays.in-ms.ship-tracking.saving}")
    public void saveShipTracks() {
        log.info("Starting ShipTracks saving process.");
        try {
            this.getCurrentShipTracks()
                    .ifPresentOrElse(
                            currentShipTracks -> {
                                this.saveNoStationaryShipTracks(currentShipTracks);
                                log.info("Saving ShipTracks succeed.");
                            },
                            () -> log.info("There are no ships to track, so all operations are skipped.")
                    );
        } catch (Exception e) {
            log.error("An error has been occurred during saving ShipTracks.", e);
        }
    }

    // todo - check the EPSILON comparison instead of Double#equals
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

    private Optional<List<ShipTrack>> getCurrentShipTracks() {
        return shipMapManager.getActiveShipMmsis()
                .flatMap(mmsis -> aisService.fetchShipsByMmsis(mmsis)
                        .map(currentShips -> currentShips.stream()
                                .map(this::mapToShipTrack)
                                .toList())
                        .or(() -> {
                            log.error("Critical: received no ships from AIS API despite passing active MMSIs='{}' from the ShipPoints map.", mmsis);
                            return Optional.empty();
                        })
                );
    }

    private ShipTrack mapToShipTrack(JsonNode ship) {
        return new ShipTrack(
                ship.get(ShipTrackConstants.MMSI).asText(),
                ship.get(AisApiConstants.LONGITUDE).asDouble(),
                ship.get(AisApiConstants.LATITUDE).asDouble()
        );
    }

}