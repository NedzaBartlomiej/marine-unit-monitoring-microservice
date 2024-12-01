package pl.bartlomiej.keycloakspibundle.common.tokenaccess;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.util.ObjectMapperResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bartlomiej.keycloakspibundle.common.config.PropertiesProvider;

public class TokenManager {
    private static final Logger log = LoggerFactory.getLogger(TokenManager.class);
    private static final TokenFetcher TOKEN_FETCHER = new TokenFetcher(new PropertiesProvider(ObjectMapperResolver.createStreamSerializer()));
    private static String token;

    public static String getToken(final KeycloakSession keycloakSession) {
        if (token == null) {
            token = TOKEN_FETCHER.fetchToken(keycloakSession);
        }
        return token;
    }

    public static void refreshToken(final KeycloakSession keycloakSession) {
        log.info("Refreshing token.");
        token = TOKEN_FETCHER.fetchToken(keycloakSession);
    }
}
