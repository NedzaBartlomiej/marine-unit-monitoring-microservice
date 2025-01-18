package pl.bartlomiej.loginservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class IdmServiceRepResolver {

    private static final Logger log = LoggerFactory.getLogger(IdmServiceRepResolver.class);
    private final List<IdmServiceRepresentation> configuredIdmServices;

    public IdmServiceRepResolver(List<IdmServiceRepresentation> configuredIdmServices) {
        this.configuredIdmServices = configuredIdmServices;
    }

    public IdmServiceRepresentation resolve(final String clientId) {
        log.debug("Resolving login service hostname, using {} client", clientId);
        return this.configuredIdmServices.stream()
                .filter(rep -> rep.getClientId().equals(clientId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Invalid clientId. Check the conformity of configuration and auth-server."));
    }
}