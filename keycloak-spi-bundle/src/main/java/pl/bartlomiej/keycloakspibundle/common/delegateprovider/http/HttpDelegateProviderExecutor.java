package pl.bartlomiej.keycloakspibundle.common.delegateprovider.http;

import com.fasterxml.jackson.databind.JsonNode;
import org.keycloak.models.KeycloakSession;

public class HttpDelegateProviderExecutor<ContextType> {

    private final HttpDelegateService httpDelegateService;

    public HttpDelegateProviderExecutor(HttpDelegateService httpDelegateService) {
        this.httpDelegateService = httpDelegateService;
    }

    public void executeHttpDelegation(final HttpDelegateProvider<ContextType> httpDelegateProvider,
                                      final ContextType context,
                                      final KeycloakSession keycloakSession) {
        JsonNode jsonResponse = this.request(httpDelegateProvider, context, keycloakSession);
        this.handleResponse(httpDelegateProvider, context, jsonResponse);
    }

    private JsonNode request(HttpDelegateProvider<ContextType> httpDelegateProvider, ContextType context, KeycloakSession keycloakSession) {
        return this.httpDelegateService.sendRequest(
                httpDelegateProvider.buildSimpleHttp(context),
                httpDelegateProvider.buildRequestBody(context),
                keycloakSession
        );
    }

    private void handleResponse(HttpDelegateProvider<ContextType> httpDelegateProvider, ContextType context, JsonNode jsonResponse) {
        if (!MumResponseModelUtil.getSuccess(jsonResponse)) {
            httpDelegateProvider.handleFailure(jsonResponse, context);
            return;
        }
        httpDelegateProvider.handleSuccess(jsonResponse, context);
    }
}