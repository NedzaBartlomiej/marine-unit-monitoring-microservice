package pl.bartlomiej.apiservice.shiptracking.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.bartlomiej.apiservice.shiptracking.ShipTrack;

import java.time.LocalDateTime;
import java.util.List;

public interface ShipTrackService {
    List<ShipTrack> getShipTracks(String userId, LocalDateTime from, LocalDateTime to);

    SseEmitter getShipTrackStream(String userId, String xApiKey);

    void clearShipHistory(String mmsi);
}
