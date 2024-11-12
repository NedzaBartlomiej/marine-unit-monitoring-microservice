package pl.bartlomiej.protectionservice.iploginprotection.service;

import com.netflix.discovery.EurekaClient;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pl.bartlomiej.mummicroservicecommons.globalidmservice.internal.keycloakidm.KeycloakProperties;

import java.util.List;

/**
 * This class resolving the service which performs user login functions, basing on the
 * auth server clientId
 */
@Component
public class LoginServiceResolver {

    private static final Logger log = LoggerFactory.getLogger(LoginServiceResolver.class);
    private final RealmResource realmResource;
    private final EurekaClient eurekaClient;

    LoginServiceResolver(KeycloakProperties properties, Keycloak keycloak, EurekaClient eurekaClient) {
        this.eurekaClient = eurekaClient;
        this.realmResource = keycloak.realm(properties.realmName());
    }

    /**
     * @param clientId this should be real auth-server clientId which:
     *                 should match this regex: "^[a-z]+(-[a-z]+)*-client$",
     *                 and the first part of the clientId should be equal to
     *                 real service name, and it's hostname e.g.: <p>
     *                 service name = api-service -> clientId = api-service-client
     * @return A hostname used to call log in services requests.
     * When the clientId doesn't meet the requirements this method will fail.
     * Make sure you've correctly configured client in your auth server.
     */
    public String resolveHostname(final String clientId) {
        log.debug("Resolving login service hostname, using {} client", clientId);
        this.validateClientId(clientId);
        String hostname = this.produceHostname(clientId);
        this.validateResolution(hostname);
        return hostname;
    }

    private void validateClientId(final String clientId) {
        log.debug("Validating client-id: {}", clientId);
        List<ClientRepresentation> clientRepresentations = realmResource.clients().findByClientId(clientId);
        if (clientRepresentations.isEmpty()) {
            log.error("Invalid client-id: {}", clientId);
            throw new IllegalArgumentException("Invalid client-id. Check your auth server clients.");
        }
        log.debug("Client id and hostname are correct. Resolution results are correct.");
    }

    private void validateResolution(final String hostname) {
        log.debug("Validating resolution.");
        if (eurekaClient.getApplication(hostname) == null) {
            throw new IllegalArgumentException("Invalid client-id, service not found in Eureka: " + hostname);
        }
        log.debug("Resolution is correct.");
    }

    private String produceHostname(final String clientId) {
        log.debug("Producing hostname from the client-id: {}", clientId);
        return clientId.replaceAll("-client$", "");
    }
}