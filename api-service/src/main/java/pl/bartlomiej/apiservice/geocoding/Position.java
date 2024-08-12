package pl.bartlomiej.apiservice.geocoding;

import java.io.Serializable;

public record Position(
        Double x,
        Double y
) implements Serializable {
}