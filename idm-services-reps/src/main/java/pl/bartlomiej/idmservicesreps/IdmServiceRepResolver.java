package pl.bartlomiej.idmservicesreps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class IdmServiceRepResolver {

    private static final Logger log = LoggerFactory.getLogger(IdmServiceRepResolver.class);
    private final List<IdmServiceRepresentation> configuredIdmServices;

    public IdmServiceRepResolver(List<IdmServiceRepresentation> configuredIdmServices) {
        this.configuredIdmServices = configuredIdmServices;
    }

    /**
     * Resolves the {@link IdmServiceRepresentation} for the given client identifier.
     * <p>
     * The method searches through the configured IDM services and returns the one
     * that matches the provided {@code clientId}. If no matching service is found,
     * an {@link IllegalArgumentException} is thrown.
     * </p>
     *
     * @param clientId the identifier of the client for which the IDM service should be resolved; must not be {@code null}
     * @return the {@link IdmServiceRepresentation} corresponding to the given client identifier
     * @throws IllegalArgumentException if no matching IDM service is found for the provided clientId
     */
    public IdmServiceRepresentation resolve(final String clientId) {
        log.debug("Resolving login service hostname, using {} client", clientId);
        if (clientId == null || clientId.isBlank())
            throw new IllegalArgumentException("'clientId' is null or blank.");
        return this.configuredIdmServices.stream()
                .filter(rep -> rep.getClientId().equals(clientId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Invalid 'clientId'. Check the conformity of configuration and auth-server."));
    }
}