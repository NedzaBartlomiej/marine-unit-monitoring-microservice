package pl.bartlomiej.apiservice.geocoding;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import pl.bartlomiej.apiservice.common.helper.Position;

import java.util.Optional;

import static pl.bartlomiej.apiservice.common.config.redis.RedisCacheConfig.ADDRESS_COORDS_CACHE_NAME;

@Service
public class HereGeocodeService implements GeocodeService {

    private static final String LAT = "lat";
    private static final String LNG = "lng";
    private static final int FIRST_GEOCODE_SUGGESTION = 0;
    private static final Logger log = LoggerFactory.getLogger(HereGeocodeService.class);
    private final RestClient restClient;
    private final String geocodeApiKey;
    private final String geocodeApiBaseUrl;
    private final HereGeocodeService self;

    public HereGeocodeService(@Qualifier("defaultRestClient") RestClient restClient,
                              @Value("${geocode-api.api-key}") String geocodeApiKey,
                              @Value("${geocode-api.api-base-url}") String geocodeApiBaseUrl,
                              @Lazy HereGeocodeService self) {
        this.restClient = restClient;
        this.geocodeApiKey = geocodeApiKey;
        this.geocodeApiBaseUrl = geocodeApiBaseUrl;
        this.self = self;
    }

    @Cacheable(cacheNames = ADDRESS_COORDS_CACHE_NAME)
    @Override
    public Optional<Position> getAddressCoordinates(String address) {
        log.trace("Obtaining coordinates for address='{}'", address);
        if (address == null || address.isBlank()) {
            log.trace("An empty address='{}' occurred during geocoding. Skipping geocoding process.", address);
            return Optional.empty();
        }
        log.trace("Returning a Position object for the address='{}'", address);
        return self.geocodeAddress(address)
                .flatMap(geocodeResponse -> this.mapAddress(geocodeResponse, address));
    }

    @Retryable(
            retryFor = {HttpClientErrorException.TooManyRequests.class},
            maxAttemptsExpression = "${project-properties.retry.max-attempts.too-many-requests}",
            backoff = @Backoff(
                    delayExpression = "${project-properties.retry.delays.canonical.too-many-requests}",
                    maxDelayExpression = "${project-properties.retry.delays.max.too-many-requests}",
                    multiplierExpression = "${project-properties.retry.delays.multiplier.too-many-requests}"
            )
    )
    protected Optional<JsonNode> geocodeAddress(String address) {
        log.trace("Requesting geocoding API for the address='{}' geocode", address);
        JsonNode response = restClient
                .get()
                .uri(this.getGeocodeApiUrl(address))
                .retrieve()
                .body(JsonNode.class);
        if (response == null || response.isNull() || response.isEmpty()) {
            log.error("Received invalid response from geocoding API.");
            return Optional.empty();
        }
        log.trace("Successfully received geocoding info from the API about the address='{}'", address);
        return Optional.of(response);
    }

    private Optional<Position> mapAddress(JsonNode response, String address) {
        JsonNode itemsNode = response.get("items");

        if (itemsNode == null || itemsNode.isNull()) {
            log.error("Unexpected behavior from Geocoding API – missing 'items' field in response for address='{}'. Raw response: {}", address, response.toPrettyString());
            return Optional.empty();
        }

        if (!itemsNode.isArray()) {
            log.error("Invalid response structure from Geocoding API – 'items' is not an array for address='{}'. Raw response: {}", address, response.toPrettyString());
            return Optional.empty();
        }

        if (itemsNode.isEmpty()) {
            log.trace("No geocode suggestions returned for address='{}'.", address);
            return Optional.empty();
        }

        JsonNode positionNode = itemsNode.get(FIRST_GEOCODE_SUGGESTION).get("position");
        if (positionNode == null || !positionNode.has(LNG) || !positionNode.has(LAT)) {
            log.error("Missing 'position' or coordinates in Geocoding API response for address='{}'. Raw response: {}", address, response.toPrettyString());
            return Optional.empty();
        }

        return Optional.of(
                new Position(positionNode.get(LNG).asDouble(), positionNode.get(LAT).asDouble())
        );
    }


    private String getGeocodeApiUrl(String address) {
        return this.geocodeApiBaseUrl +
                "?q=" + address +
                "&apiKey=" + geocodeApiKey;
    }
}
