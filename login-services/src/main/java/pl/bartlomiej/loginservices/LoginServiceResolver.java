package pl.bartlomiej.loginservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LoginServiceResolver {

    private static final Logger log = LoggerFactory.getLogger(LoginServiceResolver.class);
    private final List<LoginServiceRepresentation> loginServiceRepresentations;

    public LoginServiceResolver(List<LoginServiceRepresentation> loginServiceRepresentations) {
        this.loginServiceRepresentations = loginServiceRepresentations;
    }

    public LoginServiceRepresentation resolve(final String clientId) {
        log.debug("Resolving login service hostname, using {} client", clientId);
        return this.loginServiceRepresentations.stream()
                .filter(rep -> rep.clientId().equals(clientId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Invalid clientId. Check the conformity of configuration and auth-server."));
    }
}