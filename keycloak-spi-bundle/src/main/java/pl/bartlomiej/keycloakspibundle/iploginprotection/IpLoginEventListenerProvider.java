package pl.bartlomiej.keycloakspibundle.iploginprotection;

import com.fasterxml.jackson.databind.JsonNode;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bartlomiej.keycloakspibundle.common.delegateprovider.http.HttpDelegateProvider;
import pl.bartlomiej.keycloakspibundle.common.delegateprovider.http.HttpDelegateProviderExecutor;
import pl.bartlomiej.keycloakspibundle.common.delegateprovider.http.MumResponseModelUtil;
import pl.bartlomiej.keycloakspibundle.common.exception.ProtectionServiceException;

import java.util.concurrent.CompletableFuture;

public class IpLoginEventListenerProvider implements EventListenerProvider, HttpDelegateProvider<Event> {

    private static final Logger log = LoggerFactory.getLogger(IpLoginEventListenerProvider.class);
    private final KeycloakSession keycloakSession;
    private final IpLoginProtectionConfig ipLoginProtectionConfig;
    private final HttpDelegateProviderExecutor<Event> httpDelegateProviderExecutor;

    public IpLoginEventListenerProvider(KeycloakSession keycloakSession, IpLoginProtectionConfig ipLoginProtectionConfig, HttpDelegateProviderExecutor<Event> httpDelegateProviderExecutor) {
        this.keycloakSession = keycloakSession;
        this.ipLoginProtectionConfig = ipLoginProtectionConfig;
        this.httpDelegateProviderExecutor = httpDelegateProviderExecutor;
    }

    @Override
    public void onEvent(Event event) {
        if (!event.getType().equals(EventType.LOGIN)) return;
        CompletableFuture.runAsync(() ->
                this.httpDelegateProviderExecutor.executeHttpDelegation(
                        this,
                        event,
                        this.keycloakSession
                )
        );
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {
    }

    @Override
    public void close() {
    }

    @Override
    public SimpleHttp buildSimpleHttp(Event context) {
        return SimpleHttp.doPost(
                this.ipLoginProtectionConfig.getProtectionServiceUrl(),
                this.keycloakSession
        );
    }

    @Override
    public Object buildRequestBody(Event context) {
        UserModel loginUser = this.keycloakSession
                .users()
                .getUserById(
                        this.keycloakSession.getContext().getRealm(),
                        context.getUserId()
                );
        return new IpLoginProtectionRequest(
                context.getIpAddress(),
                context.getUserId(),
                loginUser.getEmail(),
                context.getClientId()
        );
    }

    @Override
    public void handleSuccess(JsonNode response, Event context) {
        log.info("Successfully executed protection process with result: {}", MumResponseModelUtil.getMessage(response));
    }

    @Override
    public void handleFailure(JsonNode response, Event context) {
        throw new ProtectionServiceException("Some error occurred executing protection (from protection-service): " + MumResponseModelUtil.getMessage(response));
    }
}