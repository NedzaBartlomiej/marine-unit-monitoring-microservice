package pl.bartlomiej.apiservice.aisapi.service;

import com.fasterxml.jackson.databind.JsonNode;
import pl.bartlomiej.apiservice.aisapi.AisShip;

import java.util.List;

public interface AisService {
    List<AisShip> fetchLatestShips();

    List<JsonNode> fetchShipsByMmsis(List<String> mmsis);
}
