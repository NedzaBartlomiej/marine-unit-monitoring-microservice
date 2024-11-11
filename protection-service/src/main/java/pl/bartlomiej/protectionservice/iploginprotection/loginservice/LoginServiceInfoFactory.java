package pl.bartlomiej.protectionservice.iploginprotection.loginservice;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pl.bartlomiej.mummicroservicecommons.globalidmservice.internal.keycloakidm.KeycloakProperties;

import java.util.List;

@Component
public class LoginServiceInfoFactory {

    private static final Logger log = LoggerFactory.getLogger(LoginServiceInfoFactory.class);
    private final RealmResource realmResource;

    LoginServiceInfoFactory(KeycloakProperties properties, Keycloak keycloak) {
        this.realmResource = keycloak.realm(properties.realmName());
    }

    public LoginServiceInfo produce(final String clientId) {
        log.debug("Producing LoginServiceInfo object, using {} client", clientId);
        ClientRepresentation clientRepresentation = this.validateClientId(clientId);
        return new LoginServiceInfo(
                this.produceHostname(clientRepresentation.getClientId()),
                this.getDefaultRole(clientRepresentation)
        );
    }

    private ClientRepresentation validateClientId(final String clientId) {
        log.debug("Validating login service client-id: {}", clientId);
        List<ClientRepresentation> clientRepresentations = realmResource.clients().findByClientId(clientId);
        if (clientRepresentations.isEmpty()) {
            log.error("Invalid login service client-id: {}", clientId);
            throw new IllegalArgumentException("Invalid login service client-id. Check your auth server clients.");
        }
        return clientRepresentations.getFirst();
    }

    private String produceHostname(final String clientId) {
        log.debug("Producing hostname from the client-id: {}", clientId);
        return clientId.replaceAll("-client$", "");
    }

    private String getDefaultRole(final ClientRepresentation clientRepresentation) {
        log.debug("Acquisition of the default {} role", clientRepresentation.getClientId());
        List<RoleRepresentation> roleRepresentations = realmResource.clients().get(clientRepresentation.getId()).roles().list();
        return roleRepresentations.stream()
                .map(RoleRepresentation::getName)
                .filter(role -> role.startsWith("DEF_"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Default auth server client role not found."));
    }
}