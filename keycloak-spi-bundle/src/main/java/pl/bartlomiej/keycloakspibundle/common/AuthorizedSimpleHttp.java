package pl.bartlomiej.keycloakspibundle.common;

import org.apache.http.HttpStatus;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.models.KeycloakSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bartlomiej.keycloakspibundle.common.exception.AuthRetryException;
import pl.bartlomiej.keycloakspibundle.common.exception.HttpRequestException;
import pl.bartlomiej.keycloakspibundle.common.tokenaccess.TokenManager;

import java.io.IOException;

public class AuthorizedSimpleHttp {

    private static final Logger log = LoggerFactory.getLogger(AuthorizedSimpleHttp.class);

    public SimpleHttp.Response request(final SimpleHttp simpleHttp, final Object jsonBody, final KeycloakSession keycloakSession) {
        log.info("Processing an authorized request.");
        SimpleHttp http = simpleHttp
                .acceptJson()
                .auth(TokenManager.getToken(keycloakSession))
                .json(jsonBody);
        try (SimpleHttp.Response response = http.asResponse()) {
            log.info("Sending an authorized request.");
            if (response.getStatus() != HttpStatus.SC_UNAUTHORIZED) {
                log.info("Successful authorized request.");
                return response;
            }
            return this.retryUnauthorizedRequest(keycloakSession, http);
        } catch (IOException e) {
            throw new HttpRequestException("Authorized SimpleHttp request. (retryHttp or http) problem.", e);
        }
    }

    private SimpleHttp.Response retryUnauthorizedRequest(final KeycloakSession keycloakSession, final SimpleHttp http) throws IOException {
        log.warn("Unauthorized request, retrying request with refreshed token.");
        TokenManager.refreshToken(keycloakSession);
        http.auth(TokenManager.getToken(keycloakSession));
        try (SimpleHttp.Response retriedResponse = http.asResponse()) {
            if (retriedResponse.getStatus() == HttpStatus.SC_UNAUTHORIZED) throw new AuthRetryException();
            return retriedResponse;
        }
    }
}