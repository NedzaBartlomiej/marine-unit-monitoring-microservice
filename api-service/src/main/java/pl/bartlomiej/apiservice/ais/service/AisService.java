package pl.bartlomiej.apiservice.ais.service;

import com.fasterxml.jackson.databind.JsonNode;
import pl.bartlomiej.apiservice.ais.AisShip;
import reactor.core.publisher.Flux;

import java.util.List;

public interface AisService {
    Flux<AisShip> fetchLatestShips();

    Flux<JsonNode> fetchShipsByIdentifiers(List<String> identifiers);
}
