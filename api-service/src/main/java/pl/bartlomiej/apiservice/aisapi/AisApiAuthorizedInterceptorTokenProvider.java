package pl.bartlomiej.apiservice.aisapi;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import pl.bartlomiej.mumcommons.core.webtools.requesthandler.authorizedhandler.AuthorizedInterceptorTokenProvider;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@Service
public class AisApiAuthorizedInterceptorTokenProvider implements AuthorizedInterceptorTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(AisApiAuthorizedInterceptorTokenProvider.class);
    private final RestClient restClient;

    private final String clientId;

    private final String scope;

    private final String clientSecret;

    private final String grantType;

    private final String accessTokenApiUrl;

    public AisApiAuthorizedInterceptorTokenProvider(@Qualifier("defaultRestClient") RestClient restClient,
                                                    @Value("${ais-api.auth.client-id}") String clientId,
                                                    @Value("${ais-api.auth.scope}") String scope,
                                                    @Value("${ais-api.auth.client-secret}") String clientSecret,
                                                    @Value("${ais-api.auth.grant-type}") String grantType,
                                                    @Value("${ais-api.auth.url}") String accessTokenApiUrl) {
        this.restClient = restClient;
        this.clientId = clientId;
        this.scope = scope;
        this.clientSecret = clientSecret;
        this.grantType = grantType;
        this.accessTokenApiUrl = accessTokenApiUrl;
    }

    @Override
    public String getValidToken() {
        log.debug("Obtaining valid access token.");
        return this.extractTokenFromApiResponse(
                this.fetchAuthTokenFromApi()
        );
    }

    private JsonNode fetchAuthTokenFromApi() {
        log.debug("Fetching access token from AIS API.");
        JsonNode response = restClient
                .post()
                .uri(accessTokenApiUrl)
                .header(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE)
                .body(buildAuthBody())
                .retrieve()
                .body(JsonNode.class);
        if (response == null || response.isNull() || response.isEmpty()) {
            log.error("AIS API access token response is invalid. Response object: {}", response);
            throw new IllegalStateException("Failed to fetch access token from AIS API.");
        }
        log.debug("Successfully fetched access token from AIS API, returning valid response.");
        return response;
    }

    private String extractTokenFromApiResponse(JsonNode response) {
        log.debug("Extracting AIS API access token from valid response.");
        String accessToken = response.get("access_token").asText();
        if (accessToken == null || accessToken.isBlank()) {
            log.error("AIS API access token is invalid. Token object: {}", accessToken);
            throw new IllegalStateException("Failed to extract valid token from AIS API response.");
        }
        log.debug("Successfully extracted AIS API access token from the response, returning.");
        return accessToken;
    }

    private MultiValueMap<String, String> buildAuthBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("scope", scope);
        body.add("client_secret", clientSecret);
        body.add("grant_type", grantType);
        return body;
    }
}