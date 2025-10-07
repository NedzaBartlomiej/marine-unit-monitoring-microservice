package pl.bartlomiej.apiservice.geocoding;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
public class GeocodingApiGeocodeService implements GeocodeService {

    private static final String LAT = "lat";
    private static final String LON = "lon";
    private static final int FIRST_GEOCODE_SUGGESTION = 0;
    private final RestClient restClient;
    private final String geocodeApiKey;
    private final String geocodeApiBaseUrl;
    private final GeocodingApiGeocodeService self;

    public GeocodingApiGeocodeService(@Qualifier("defaultRestClient") RestClient restClient,
                                      @Value("${geocode-api.api-key}") String geocodeApiKey,
                                      @Value("${geocode-api.api-base-url}") String geocodeApiBaseUrl,
                                      @Lazy GeocodingApiGeocodeService self) {
        this.restClient = restClient;
        this.geocodeApiKey = geocodeApiKey;
        this.geocodeApiBaseUrl = geocodeApiBaseUrl;
        this.self = self;
    }

    @Cacheable(cacheNames = ADDRESS_COORDS_CACHE_NAME)
    @Override
    public Optional<Position> getAddressCoordinates(String address) {
        log.trace("Obtaining coordinates for address='{}'.", address);
        if (address == null || address.isBlank()) {
            log.trace("An empty address='{}' occurred during geocoding. Skipping geocoding process.", address);
            return Optional.empty();
        }
        log.trace("Obtaining a Position object for the address='{}'.", address);
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
    protected Optional<JsonNode[]> geocodeAddress(String address) {
        log.trace("Requesting geocoding API for the address='{}' geocode.", address);
        JsonNode[] response = restClient
                .get()
                .uri(this.getGeocodeApiUrl(address))
                .retrieve()
                .body(JsonNode[].class);
        if (response == null) {
            log.error("Received null response from geocoding API for the address='{}'.", address);
            return Optional.empty();
        } else if (response.length == 0) {
            log.warn("No geocode suggestions returned for address='{}'.", address);
            return Optional.empty();
        }
        log.trace("Successfully received geocoding info from the API about the address='{}'.", address);
        return Optional.of(response);
    }

    private Optional<Position> mapAddress(JsonNode[] response, String address) {
        JsonNode geocodeSuggestion = response[FIRST_GEOCODE_SUGGESTION];
        if (geocodeSuggestion == null || !geocodeSuggestion.has(LON) || !geocodeSuggestion.has(LAT)) {
            log.error("Missing 'suggestion' or coordinates in Geocoding API response for address='{}'; Raw response: {}", address, response);
            return Optional.empty();
        }

        return Optional.of(
                new Position(geocodeSuggestion.get(LON).asDouble(), geocodeSuggestion.get(LAT).asDouble())
        );
    }


    private String getGeocodeApiUrl(String address) {
        return this.geocodeApiBaseUrl +
                "?q=" + address +
                "&api_key=" + geocodeApiKey;
    }
}