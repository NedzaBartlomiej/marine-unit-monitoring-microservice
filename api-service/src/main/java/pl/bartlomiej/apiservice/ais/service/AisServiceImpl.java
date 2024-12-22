package pl.bartlomiej.apiservice.ais.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.bartlomiej.apiservice.ais.AisShip;
import pl.bartlomiej.apiservice.common.util.CommonShipFields;
import reactor.core.publisher.Flux;

import java.util.List;

import static java.util.Map.of;

@Service
public class AisServiceImpl implements AisService {

    private final WebClient webClient;
    private final long resultLimit;
    private final String apiFetchLatestUri;
    private final String apiFetchByMmsiUri;

    public AisServiceImpl(@Qualifier("aisApiAuthorizedWebClient") WebClient webClient,
                          @Value("${project-properties.external-apis.ais-api.result-limit}") long resultLimit,
                          @Value("${ais-api.latest-ais-url}") String apiFetchLatestUri,
                          @Value("${ais-api.latest-ais-bymmsi-url}") String apiFetchByMmsiUri) {
        this.webClient = webClient;
        this.resultLimit = resultLimit;
        this.apiFetchLatestUri = apiFetchLatestUri;
        this.apiFetchByMmsiUri = apiFetchByMmsiUri;
    }

    @Override
    public Flux<AisShip> fetchLatestShips() {
        return webClient
                .get()
                .uri(apiFetchLatestUri)
                .retrieve()
                .bodyToFlux(AisShip.class)
                .take(resultLimit);
    }

    @Override
    public Flux<JsonNode> fetchShipsByIdentifiers(List<String> identifiers) {
        return webClient
                .post()
                .uri(apiFetchByMmsiUri)
                .bodyValue(of(CommonShipFields.MMSI, identifiers.toArray()))
                .retrieve()
                .bodyToFlux(JsonNode.class);
    }
}