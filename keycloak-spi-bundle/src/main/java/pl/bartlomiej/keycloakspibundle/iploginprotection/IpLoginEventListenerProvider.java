package pl.bartlomiej.keycloakspibundle.iploginprotection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bartlomiej.keycloakspibundle.common.KeycloakAccessTokenProvider;

import java.io.IOException;
import java.util.Map;

// todo - refactor
public class IpLoginEventListenerProvider implements EventListenerProvider {

    private static final Logger log = LoggerFactory.getLogger(IpLoginEventListenerProvider.class);
    private final KeycloakAccessTokenProvider tokenProvider = new KeycloakAccessTokenProvider();
    private final KeycloakSession keycloakSession;

    public IpLoginEventListenerProvider(KeycloakSession keycloakSession) {
        this.keycloakSession = keycloakSession;
    }

    @Override
    public void onEvent(Event event) {
        if (!event.getType().equals(EventType.LOGIN)) {
            return;
        }
        String token = tokenProvider.getToken(keycloakSession);
        log.info(token);
        String ipAddress = event.getIpAddress();
        String uid = event.getUserId();
        String email = keycloakSession.users()
                .getUserById(keycloakSession.getContext().getRealm(), uid)
                .getEmail();
        String clientId = event.getClientId();
        Map<String, String> protectionDetailsJson = Map.of(
                "ipAddress", ipAddress,
                "uid", uid,
                "email", email,
                "clientId", clientId
        );
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            log.debug("Sending request to protection-service for login protection execution, with body: {}", protectionDetailsJson);
            SimpleHttp serviceRequest = SimpleHttp.doPost("http://protection-service:8085/v1/ip-login-protection/rpc/protect-login",
                            keycloakSession)
                    .acceptJson()
                    .header(org.apache.http.HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .json(objectMapper.valueToTree(protectionDetailsJson));
            log.info("Executed ip login protection, results: {}", serviceRequest.asJson().toPrettyString());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("Something go wrong executing ip login protection service call.", e);
        }
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {

    }

    @Override
    public void close() {

    }
}