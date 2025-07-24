package pl.bartlomiej.apiservice.aisapi.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pl.bartlomiej.apiservice.aisapi.AisShip;
import pl.bartlomiej.apiservice.common.util.CommonShipFields;

import java.util.List;
import java.util.Optional;

import static java.util.Map.of;

@Slf4j
@Service
public class DefaultAisService implements AisService {

    private final RestClient restClient;
    private final long resultLimit;
    private final String apiFetchLatestUri;
    private final String apiFetchByMmsiUri;

    public DefaultAisService(@Qualifier("aisApiAuthorizedRestClient") RestClient restClient,
                             @Value("${project-properties.external-apis.ais-api.result-limit}") long resultLimit,
                             @Value("${ais-api.latest-ais-url}") String apiFetchLatestUri,
                             @Value("${ais-api.latest-ais-bymmsi-url}") String apiFetchByMmsiUri) {
        this.restClient = restClient;
        this.resultLimit = resultLimit;
        this.apiFetchLatestUri = apiFetchLatestUri;
        this.apiFetchByMmsiUri = apiFetchByMmsiUri;
    }

    @Override
    public Optional<List<AisShip>> fetchLatestShips() {
        log.debug("Fetching latest ships from AIS API.");
        List<AisShip> response = restClient
                .get()
                .uri(apiFetchLatestUri)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        if (response == null || response.isEmpty()) {
            log.warn("Fetched latest ships list from AIS API is null or empty, returning Optional.empty().");
            return Optional.empty();
        }
        log.debug("Successfully fetched latest ships from AIS API, returning.");
        return Optional.of(this.limitLatestShipsResponse(response, this.resultLimit));
    }

    /**
     *
     * @param aisShips List that contains AIS Ships from the AIS API response.
     * @return Limited by limit established in the properties file,
     * list that contains AIS Ships from the AIS API response,
     * due to the Geocoding API free requests limits.
     */
    private List<AisShip> limitLatestShipsResponse(List<AisShip> aisShips, long resultLimit) {
        log.trace("Limiting AIS Ships response.");
        return aisShips.stream()
                .limit(resultLimit)
                .toList();
    }

    @Override
    public Optional<List<JsonNode>> fetchShipsByMmsis(List<String> mmsis) {
        log.debug("Trying to fetch ships by passed mmsi list from AIS API.");
        if (mmsis == null || mmsis.isEmpty()) {
            log.warn("Method fetchShipsByMmsis called with null or empty mmsi list - returning Optional.empty().");
            return Optional.empty();
        }
        List<JsonNode> response = restClient
                .post()
                .uri(apiFetchByMmsiUri)
                .body(of(CommonShipFields.MMSI, mmsis.toArray()))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        if (response == null || response.isEmpty()) {
            log.warn("Fetched ships by passed mmsi list, list is null or empty - returning Optional.empty().");
            return Optional.empty();
        }
        log.debug("Successfully fetched ships by passed mmsi list from AIS API, returning.");
        return Optional.of(response);
    }
}