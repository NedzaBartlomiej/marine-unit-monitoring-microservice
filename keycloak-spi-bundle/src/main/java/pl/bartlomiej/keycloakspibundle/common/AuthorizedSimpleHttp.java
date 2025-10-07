package pl.bartlomiej.keycloakspibundle.common;

import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.models.KeycloakSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bartlomiej.keycloakspibundle.common.exception.HttpRequestException;
import pl.bartlomiej.keycloakspibundle.common.tokenaccess.KeycloakTokenStorage;

import java.io.IOException;

public class AuthorizedSimpleHttp {

    private static final Logger log = LoggerFactory.getLogger(AuthorizedSimpleHttp.class);
    private final KeycloakTokenStorage keycloakTokenStorage;

    public AuthorizedSimpleHttp(KeycloakTokenStorage keycloakTokenStorage) {
        this.keycloakTokenStorage = keycloakTokenStorage;
    }

    public SimpleHttp.Response request(final SimpleHttp simpleHttp, final Object jsonBody, final KeycloakSession keycloakSession) {
        try {
            log.debug("Processing an authorized request.");
            return simpleHttp
                    .acceptJson()
                    .auth(keycloakTokenStorage.getValidToken(keycloakSession))
                    .json(jsonBody)
                    .asResponse();
        } catch (IOException e) {
            throw new HttpRequestException("Authorized SimpleHttp request. An error occurred:", e);
        }
    }
}