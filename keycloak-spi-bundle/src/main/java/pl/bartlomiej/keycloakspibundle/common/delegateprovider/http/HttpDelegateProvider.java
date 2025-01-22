package pl.bartlomiej.keycloakspibundle.common.delegateprovider.http;

import com.fasterxml.jackson.databind.JsonNode;
import org.keycloak.broker.provider.util.SimpleHttp;

public interface HttpDelegateProvider<ContextType> {

    SimpleHttp buildSimpleHttp(ContextType context);

    Object buildRequestBody(ContextType context);

    void handleSuccess(JsonNode response, ContextType context);

    void handleFailure(JsonNode response, ContextType context);
}
