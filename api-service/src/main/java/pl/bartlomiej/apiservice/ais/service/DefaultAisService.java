package pl.bartlomiej.apiservice.ais.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pl.bartlomiej.apiservice.ais.AisShip;
import pl.bartlomiej.apiservice.common.util.CommonShipFields;

import java.util.List;
import java.util.Objects;

import static java.util.Map.of;

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
    public List<AisShip> fetchLatestShips() {
        List<AisShip> aisShips = restClient
                .get()
                .uri(apiFetchLatestUri)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        return Objects.requireNonNull(aisShips, "AisShip list from the API is null.")
                .stream().limit(resultLimit).toList();
    }

    @Override
    public List<JsonNode> fetchShipsByMmsis(List<String> mmsis) {
        return restClient
                .post()
                .uri(apiFetchByMmsiUri)
                .body(of(CommonShipFields.MMSI, mmsis.toArray()))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }
}