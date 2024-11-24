package pl.bartlomiej.keycloakspibundle.iploginprotection;

import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// todo - refactor
public class IpLoginEventListenerProvider implements EventListenerProvider {

    private static final Logger log = LoggerFactory.getLogger(IpLoginEventListenerProvider.class);
    private final KeycloakSession keycloakSession;
    private final ProtectionServiceRequestService requestService;

    public IpLoginEventListenerProvider(KeycloakSession keycloakSession, ProtectionServiceRequestService requestService) {
        this.keycloakSession = keycloakSession;
        this.requestService = requestService;
    }

    @Override
    public void onEvent(Event event) {
        if (!event.getType().equals(EventType.LOGIN)) return;
        log.info("Login event detected. Executing IP login protection.");

        IpLoginProtectionRequest protectionRequest = this.buildProtectionRequest(event);
        SimpleHttp protectionResponse = this.requestService.sendProtectionRequest(protectionRequest);
        this.requestService.handleProtectionResponse(protectionResponse);
    }

    private IpLoginProtectionRequest buildProtectionRequest(final Event event) {
        log.info("Producing protection request details object.");
        return new IpLoginProtectionRequest(
                event.getIpAddress(),
                event.getUserId(),
                this.getUserModel(event).getEmail(),
                event.getClientId()
        );
    }

    private UserModel getUserModel(final Event event) {
        log.info("Getting UserModel from the event.");
        return keycloakSession.users()
                .getUserById(keycloakSession.getContext().getRealm(), event.getUserId());
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {
    }

    @Override
    public void close() {
    }
}