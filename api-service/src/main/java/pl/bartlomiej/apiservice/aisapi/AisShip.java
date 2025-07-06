package pl.bartlomiej.apiservice.aisapi;

import pl.bartlomiej.apiservice.aisapi.nested.Geometry;
import pl.bartlomiej.apiservice.aisapi.nested.Properties;

public record AisShip(
        String type,
        Geometry geometry,
        Properties properties) {
}