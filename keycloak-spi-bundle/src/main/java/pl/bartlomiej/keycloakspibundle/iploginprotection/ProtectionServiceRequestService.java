package pl.bartlomiej.keycloakspibundle.iploginprotection;

import com.fasterxml.jackson.databind.JsonNode;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.events.Event;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bartlomiej.keycloakspibundle.common.AuthorizedSimpleHttp;
import pl.bartlomiej.keycloakspibundle.common.exception.HttpRequestException;
import pl.bartlomiej.keycloakspibundle.common.exception.ProtectionServiceException;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class ProtectionServiceRequestService {

    private final IpLoginProtectionProperties ipLoginProtectionProperties;
    public static final String SUCCESS_RESP_FIELD = "success";
    public static final String MESSAGE_RESP_FIELD = "message";
    private static final Logger log = LoggerFactory.getLogger(ProtectionServiceRequestService.class);
    private final KeycloakSession keycloakSession;
    private final AuthorizedSimpleHttp authorizedSimpleHttp;

    public ProtectionServiceRequestService(KeycloakSession keycloakSession,
                                           AuthorizedSimpleHttp authorizedSimpleHttp,
                                           IpLoginProtectionProperties ipLoginProtectionProperties) {
        this.keycloakSession = keycloakSession;
        this.authorizedSimpleHttp = authorizedSimpleHttp;
        this.ipLoginProtectionProperties = ipLoginProtectionProperties;
    }

    public CompletableFuture<SimpleHttp.Response> sendProtectionRequest(final IpLoginProtectionRequest protectionRequest) {
        log.info("Requesting to protection service.");
        SimpleHttp protectionHttp = SimpleHttp.doPost(
                this.ipLoginProtectionProperties.protectionServiceUrl(),
                keycloakSession);
        return CompletableFuture.supplyAsync(
                () -> authorizedSimpleHttp.request(protectionHttp, protectionRequest, keycloakSession),
                Executors.newVirtualThreadPerTaskExecutor()
        );
    }

    public void handleProtectionResponse(final SimpleHttp.Response response) {
        log.info("Handling protection service response.");
        try {
            JsonNode json = response.asJson();
            JsonNode success = json.get(SUCCESS_RESP_FIELD);
            JsonNode message = json.get(MESSAGE_RESP_FIELD);

            if (success.asBoolean()) {
                log.info("Successfully executed protection process with result: {}", message);
                return;
            }
            throw new ProtectionServiceException("Some error occurred executing protection (from protection-service): " + message);

        } catch (IOException e) {
            throw new HttpRequestException("Protection service request problem.", e);
        }
    }

    public IpLoginProtectionRequest buildProtectionRequest(final Event event, final UserModel userModel) {
        log.info("Producing protection request details object.");
        return new IpLoginProtectionRequest(
                event.getIpAddress(),
                event.getUserId(),
                userModel.getEmail(),
                event.getClientId()
        );
    }
}