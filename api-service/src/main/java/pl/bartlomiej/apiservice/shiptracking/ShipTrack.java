package pl.bartlomiej.apiservice.shiptracking;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

@Document(collection = "ship_tracks")
public class ShipTrack {

    private final LocalDateTime readingTime = now();
    @Id
    private String mmsi;
    private Double x;
    private Double y;

    public ShipTrack(String mmsi, Double x, Double y) {
        this.mmsi = mmsi;
        this.x = x;
        this.y = y;
    }

    public ShipTrack() {
    }

    public String getMmsi() {
        return mmsi;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public LocalDateTime getReadingTime() {
        return readingTime;
    }
}