package pl.bartlomiej.apiservice.aisapi.service;

import com.fasterxml.jackson.databind.JsonNode;
import pl.bartlomiej.apiservice.aisapi.AisShip;

import java.util.List;
import java.util.Optional;

public interface AisService {
    Optional<List<AisShip>> fetchLatestShips();

    Optional<List<JsonNode>> fetchShipsByMmsis(List<String> mmsis);
}
