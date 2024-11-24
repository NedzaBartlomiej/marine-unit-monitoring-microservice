package pl.bartlomiej.keycloakspibundle.iploginprotection;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.models.KeycloakSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bartlomiej.keycloakspibundle.common.KeycloakAccessTokenProvider;

import java.io.IOException;

public class ProtectionServiceRequestService {

    // todo - extract to config file
    public static final String BEARER_PREF = "Bearer ";
    public static final String IP_PROTECTION_URL = "http://protection-service:8085/v1/ip-login-protection/rpc/protect-login";
    public static final String SUCCESS_RESP_FIELD = "success";
    public static final String MESSAGE_RESP_FIELD = "message";

    private static final Logger log = LoggerFactory.getLogger(ProtectionServiceRequestService.class);
    private final KeycloakAccessTokenProvider tokenProvider;
    private final KeycloakSession keycloakSession;

    public ProtectionServiceRequestService(KeycloakAccessTokenProvider tokenProvider, KeycloakSession keycloakSession) {
        this.tokenProvider = tokenProvider;
        this.keycloakSession = keycloakSession;
    }

    public SimpleHttp sendProtectionRequest(final IpLoginProtectionRequest protectionRequest) {
        log.info("Requesting to protection service.");
        return SimpleHttp.doPost(IP_PROTECTION_URL, keycloakSession)
                .acceptJson()
                .header(HttpHeaders.AUTHORIZATION,
                        BEARER_PREF + tokenProvider.getToken(keycloakSession))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .json(protectionRequest);
    }

    public void handleProtectionResponse(final SimpleHttp protectionResponse) {
        log.info("Handling protection service response.");
        try {
            JsonNode json = protectionResponse.asJson();
            JsonNode success = json.get(SUCCESS_RESP_FIELD);
            JsonNode message = json.get(MESSAGE_RESP_FIELD);

            if (success.asBoolean()) {
                log.info("Successfully executed protection process with result: {}", message);
                return;
            }
            log.warn("Some error occurred executing protection (from protection-service): {}", message);

        } catch (IOException e) {
            log.warn("Some unhandled, internal error occurred handling the protection response.", e);
        }
    }
}
