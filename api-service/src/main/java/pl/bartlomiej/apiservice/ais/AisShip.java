package pl.bartlomiej.apiservice.ais;

import pl.bartlomiej.apiservice.ais.nested.Geometry;
import pl.bartlomiej.apiservice.ais.nested.Properties;

public record AisShip(
        String type,
        Geometry geometry,
        Properties properties) {
}