package pl.bartlomiej.apiservice.shippoint;

import com.fasterxml.jackson.annotation.JsonInclude;
import pl.bartlomiej.apiservice.common.helper.Position;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ShipPoint(

        String mmsi,
        String name,
        Double pointX,
        Double pointY,
        String destinationName,
        Position destinationPosition) implements Serializable {
}
