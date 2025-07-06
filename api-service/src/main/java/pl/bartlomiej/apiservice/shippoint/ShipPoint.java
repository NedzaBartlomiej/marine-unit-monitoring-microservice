package pl.bartlomiej.apiservice.shippoint;

import java.io.Serializable;

public record ShipPoint(

        String mmsi,
        String name,
        Double pointX,
        Double pointY,
        String destinationName,
        Double destinationX,
        Double destinationY) implements Serializable {
}
