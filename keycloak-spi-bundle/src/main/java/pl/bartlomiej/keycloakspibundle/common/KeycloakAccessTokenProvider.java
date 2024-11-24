package pl.bartlomiej.keycloakspibundle.common;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.models.KeycloakSession;

import java.io.IOException;

// todo refactor, and do something to don't call API every method invocation -> (https://chatgpt.com/c/67439970-1e8c-8001-a200-5a426726bcd0)
public class KeycloakAccessTokenProvider {

    // todo - extract to config file
    private static final String TOKEN_URL = "http://keycloak.pl:8080/realms/marine-unit-monitoring-master/protocol/openid-connect/token";
    private static final String CLIENT_ID = "protection-service-client";
    private static final String CLIENT_SECRET = "NfRXeBKAv4NCy3klk3XHAUDkSn4W4kha";

    public String getToken(KeycloakSession keycloakSession) {
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
            throw new RuntimeException("Something go wrong fetching access token from the keycloak server.", e);
        }
    }
}