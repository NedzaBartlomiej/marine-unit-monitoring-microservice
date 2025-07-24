package pl.bartlomiej.apiservice.common.helper;

import java.io.Serializable;

public record Position(
        Double x,
        Double y
) implements Serializable {
}