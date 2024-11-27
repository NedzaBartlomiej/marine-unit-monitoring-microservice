package pl.bartlomiej.keycloakspibundle.common.tokenaccess;

import org.keycloak.models.KeycloakSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenManager {
    private static final Logger log = LoggerFactory.getLogger(TokenManager.class);
    private static String token;

    public static String getToken(final KeycloakSession keycloakSession) {
        if (token == null) {
            token = new TokenFetcher().fetchToken(keycloakSession);
        }
        return token;
    }

    public static void refreshToken(final KeycloakSession keycloakSession) {
        log.info("Refreshing token.");
        token = new TokenFetcher().fetchToken(keycloakSession);
    }
}
