package pl.bartlomiej.keycloakspibundle.iploginprotection;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        IpLoginProtectionRequest protectionRequest = this.requestService
                .buildProtectionRequest(event, this.getUserModel(event));
        this.requestService.sendProtectionRequest(protectionRequest, keycloakSession)
                .thenAccept(this.requestService::handleProtectionResponse);
    }

    private UserModel getUserModel(final Event event) {
        log.info("Fetching UserModel from the event.");
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