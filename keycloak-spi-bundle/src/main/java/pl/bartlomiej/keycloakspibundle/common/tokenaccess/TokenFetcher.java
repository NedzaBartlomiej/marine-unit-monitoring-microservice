package pl.bartlomiej.keycloakspibundle.common.tokenaccess;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.models.KeycloakSession;
import pl.bartlomiej.keycloakspibundle.common.exception.HttpRequestException;

import java.io.IOException;

public class TokenFetcher {

    // todo - extract to config file
    private static final String TOKEN_URL = "http://keycloak.pl:8080/realms/marine-unit-monitoring-master/protocol/openid-connect/token";
    private static final String CLIENT_ID = "protection-service-client";
    private static final String CLIENT_SECRET = "NfRXeBKAv4NCy3klk3XHAUDkSn4W4kha";
    private static final Log log = LogFactory.getLog(TokenFetcher.class);

    TokenFetcher() {
    }

    String fetchToken(final KeycloakSession keycloakSession) {
        log.info("Fetching token.");
        try {
            return SimpleHttp.doPost(TOKEN_URL, keycloakSession)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
                    .param("client_id", CLIENT_ID)
                    .param("client_secret", CLIENT_SECRET)
                    .param("grant_type", "client_credentials")
                    .asJson()
                    .get("access_token")
                    .asText();
        } catch (IOException e) {
            throw new HttpRequestException("Access token request.", e);
        }
    }
}