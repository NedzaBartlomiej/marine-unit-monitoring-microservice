package pl.bartlomiej.keycloakspibundle.common.tokenaccess;

import org.keycloak.models.KeycloakSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeycloakTokenManager {
    private static final Logger log = LoggerFactory.getLogger(KeycloakTokenManager.class);
    private final KeycloakTokenFetcher keycloakTokenFetcher;
    private String token;

    public KeycloakTokenManager(KeycloakTokenFetcher keycloakTokenFetcher) {
        this.keycloakTokenFetcher = keycloakTokenFetcher;
    }

    public String getToken(final KeycloakSession keycloakSession) {
        if (token == null) {
            token = keycloakTokenFetcher.fetchToken(keycloakSession);
        }
        return token;
    }

    public void refreshToken(final KeycloakSession keycloakSession) {
        log.info("Refreshing token.");
        token = keycloakTokenFetcher.fetchToken(keycloakSession);
    }
}
