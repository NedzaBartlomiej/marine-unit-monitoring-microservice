package pl.bartlomiej.keycloakspibundle.common.tokenaccess;

import org.keycloak.models.KeycloakSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

public class KeycloakTokenManager {
    private static final Logger log = LoggerFactory.getLogger(KeycloakTokenManager.class);
    private final KeycloakTokenFetcher keycloakTokenFetcher;
    private final AtomicReference<String> token = new AtomicReference<>();

    public KeycloakTokenManager(KeycloakTokenFetcher keycloakTokenFetcher) {
        this.keycloakTokenFetcher = keycloakTokenFetcher;
    }

    public String getToken(final KeycloakSession keycloakSession) {
        return token.updateAndGet(current -> {
            if (current == null) {
                log.debug("Token is null.");
                return keycloakTokenFetcher.fetchToken(keycloakSession);
            }
            return current;
        });
    }

    public void refreshToken(final KeycloakSession keycloakSession) {
        log.info("Refreshing token.");
        token.set(keycloakTokenFetcher.fetchToken(keycloakSession));
    }
}
