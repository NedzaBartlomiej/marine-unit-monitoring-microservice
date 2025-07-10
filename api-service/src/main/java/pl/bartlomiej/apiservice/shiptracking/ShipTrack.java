package pl.bartlomiej.apiservice.shiptracking;

import lombok.Getter;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Document("shipTracks")
public class ShipTrack {

    private String id;
    private final LocalDateTime readingTime;
    private final String mmsi;
    private final Double x;
    private final Double y;

    @PersistenceCreator
    ShipTrack(String id, LocalDateTime readingTime, String mmsi, Double x, Double y) {
        this.id = id;
        this.readingTime = readingTime;
        this.mmsi = mmsi;
        this.x = x;
        this.y = y;
    }

    public ShipTrack(String mmsi, Double x, Double y) {
        this.readingTime = LocalDateTime.now();
        this.mmsi = mmsi;
        this.x = x;
        this.y = y;
    }
}