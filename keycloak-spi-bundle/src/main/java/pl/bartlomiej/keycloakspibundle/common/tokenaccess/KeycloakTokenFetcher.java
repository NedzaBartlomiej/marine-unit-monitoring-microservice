package pl.bartlomiej.keycloakspibundle.common.tokenaccess;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.models.KeycloakSession;
import pl.bartlomiej.keycloakspibundle.common.exception.HttpRequestException;

import java.io.IOException;

public class KeycloakTokenFetcher {

    private final KeycloakTokenParams keycloakTokenParams;
    private static final Log log = LogFactory.getLog(KeycloakTokenFetcher.class);

    public KeycloakTokenFetcher(KeycloakTokenParams keycloakTokenParams) {
        this.keycloakTokenParams = keycloakTokenParams;
    }

    String fetchToken(final KeycloakSession keycloakSession) {
        log.info("Fetching token.");
        try {
            return SimpleHttp.doPost(this.keycloakTokenParams.tokenUrl(), keycloakSession)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
                    .param("client_id", this.keycloakTokenParams.clientId())
                    .param("client_secret", this.keycloakTokenParams.clientSecret())
                    .param("grant_type", "client_credentials")
                    .asJson()
                    .get("access_token")
                    .asText();
        } catch (IOException e) {
            throw new HttpRequestException("Access token request.", e);
        }
    }
}