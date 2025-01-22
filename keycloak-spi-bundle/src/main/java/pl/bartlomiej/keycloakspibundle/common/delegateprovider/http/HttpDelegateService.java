package pl.bartlomiej.keycloakspibundle.common.delegateprovider.http;

import com.fasterxml.jackson.databind.JsonNode;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.models.KeycloakSession;
import pl.bartlomiej.keycloakspibundle.common.AuthorizedSimpleHttp;

import java.io.IOException;

public class HttpDelegateService {

    private final AuthorizedSimpleHttp authorizedSimpleHttp;

    public HttpDelegateService(AuthorizedSimpleHttp authorizedSimpleHttp) {
        this.authorizedSimpleHttp = authorizedSimpleHttp;
    }

    public <T> JsonNode sendRequest(final SimpleHttp simpleHttp,
                                    T requestBody,
                                    final KeycloakSession keycloakSession) {
        try {
            return this.authorizedSimpleHttp.request(
                    simpleHttp,
                    requestBody,
                    keycloakSession
            ).asJson();
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when parsing response to json.", e);
        }
    }
}
