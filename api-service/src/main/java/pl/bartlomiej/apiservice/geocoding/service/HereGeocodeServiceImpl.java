package pl.bartlomiej.apiservice.geocoding.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.bartlomiej.apiservice.geocoding.Position;
import reactor.core.publisher.Flux;

import static pl.bartlomiej.apiservice.common.config.RedisCacheConfig.ADDRESS_COORDS_CACHE_NAME;

@Service
public class HereGeocodeServiceImpl implements GeocodeService {

    private static final String LAT = "lat";
    private static final String LNG = "lng";
    private static final int FIRST_GEOCODE_SUGGESTION = 0;
    private static final Logger log = LoggerFactory.getLogger(HereGeocodeServiceImpl.class);
    private final WebClient webClient;
    private final String geocodeApiKey;
    private final String geocodeApiBaseUrl;

    public HereGeocodeServiceImpl(WebClient webClient,
                                  @Value("${geocode-api.api-key}") String geocodeApiKey,
                                  @Value("${geocode-api.api-base-url}") String geocodeApiBaseUrl) {
        this.webClient = webClient;
        this.geocodeApiKey = geocodeApiKey;
        this.geocodeApiBaseUrl = geocodeApiBaseUrl;
    }

    @Cacheable(cacheNames = ADDRESS_COORDS_CACHE_NAME)
    public Flux<Position> getAddressCoordinates(String address) {
        return this.retrieveGeocodeFromApi(address)
                .map(response -> this.extractPositionFromResponse(response, address))
                .cache();
    }

    @NonNull
    private Flux<JsonNode> retrieveGeocodeFromApi(String address) {
        if (address == null || address.trim().isEmpty()) {
            log.error("Null address, skipping request sending.");
            return Flux.just(this.createDefaultPositionNode());
        }
        return webClient
                .get()
                .uri(this.buildGeocodeApiUrl(address))
                .retrieve()
                .bodyToFlux(JsonNode.class);
    }

    private Position extractPositionFromResponse(JsonNode response, String address) {
        try {
            JsonNode position = response.get("items").get(FIRST_GEOCODE_SUGGESTION).get("position");
            return new Position(position.get(LNG).asDouble(), position.get(LAT).asDouble());
        } catch (NullPointerException e) {
            log.error("Geocode not found for: {}", address);
            return new Position(0.0, 0.0);
        }
    }

    private String buildGeocodeApiUrl(String address) {
        return this.geocodeApiBaseUrl +
                "?q=" + address +
                "&apiKey=" + geocodeApiKey;
    }

    private JsonNode createDefaultPositionNode() {
        ObjectNode positionNode = JsonNodeFactory.instance.objectNode();
        positionNode.put(LNG, 0.0);
        positionNode.put(LAT, 0.0);
        return positionNode;
    }
}
