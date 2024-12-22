package pl.bartlomiej.apiservice.ais;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import pl.bartlomiej.mumcommons.core.webtools.requesthandler.authorizedhandler.reactor.AuthorizedExchangeTokenProvider;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.web.reactive.function.BodyInserters.fromFormData;

@Service
public class AisApiAuthorizedExchangeTokenProvider implements AuthorizedExchangeTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(AisApiAuthorizedExchangeTokenProvider.class);
    private final WebClient webClient;

    private final String clientId;

    private final String scope;

    private final String clientSecret;

    private final String grantType;

    private final String accessTokenApiUrl;

    public AisApiAuthorizedExchangeTokenProvider(@Qualifier("defaultWebClient") WebClient webClient,
                                                 @Value("${ais-api.auth.client-id}") String clientId,
                                                 @Value("${ais-api.auth.scope}") String scope,
                                                 @Value("${ais-api.auth.client-secret}") String clientSecret,
                                                 @Value("${ais-api.auth.grant-type}") String grantType,
                                                 @Value("${ais-api.auth.url}") String accessTokenApiUrl) {
        this.webClient = webClient;
        this.clientId = clientId;
        this.scope = scope;
        this.clientSecret = clientSecret;
        this.grantType = grantType;
        this.accessTokenApiUrl = accessTokenApiUrl;
    }

    private MultiValueMap<String, String> buildAuthBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("scope", scope);
        body.add("client_secret", clientSecret);
        body.add("grant_type", grantType);
        return body;
    }

    @Override
    public Mono<String> getValidToken() {
        log.info("Access token has refreshed now.");
        return this.fetchAuthTokenFromApi()
                .map(this::extractTokenFromApiResponse);
    }

    private Mono<JsonNode> fetchAuthTokenFromApi() {
        return webClient
                .post()
                .uri(accessTokenApiUrl)
                .header(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE)
                .body(fromFormData(buildAuthBody()))
                .retrieve()
                .bodyToMono(JsonNode.class);
    }

    private String extractTokenFromApiResponse(JsonNode response) {
        return response.get("access_token").asText();
    }
}