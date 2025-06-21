package pl.bartlomiej.apiservice.ais.service;

import com.fasterxml.jackson.databind.JsonNode;
import pl.bartlomiej.apiservice.ais.AisShip;

import java.util.List;

public interface AisService {
    List<AisShip> fetchLatestShips();

    List<JsonNode> fetchShipsByMmsis(List<String> mmsis);
}
