package pl.bartlomiej.protectionservice.iploginprotection.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pl.bartlomiej.mummicroservicecommons.config.loginservicereps.LoginServiceRepresentation;
import pl.bartlomiej.mummicroservicecommons.config.loginservicereps.LoginServiceRepsProperties;

@Component
class LoginServiceResolver {

    private static final Logger log = LoggerFactory.getLogger(LoginServiceResolver.class);
    private final LoginServiceRepsProperties loginServiceRepsProperties;

    LoginServiceResolver(LoginServiceRepsProperties loginServiceRepsProperties) {
        this.loginServiceRepsProperties = loginServiceRepsProperties;
    }

    LoginServiceRepresentation resolve(final String clientId) {
        log.debug("Resolving login service hostname, using {} client", clientId);
        return loginServiceRepsProperties.loginServiceRepresentations().stream()
                .filter(rep -> rep.clientId().equals(clientId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Invalid clientId. Check the conformity of configuration and auth-server."));
    }
}