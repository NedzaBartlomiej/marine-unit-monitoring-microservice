package pl.bartlomiej.protectionservice.iploginprotection.common.config;

import pl.bartlomiej.mummicroservicecommons.globalidmservice.external.keycloakidm.servlet.KeycloakService;
import pl.bartlomiej.mummicroservicecommons.webtools.retryclient.unauthorized.external.RetryClientTokenProvider;

public class KeycloakRetryClientTokenProvider implements RetryClientTokenProvider {

    private final KeycloakService keycloakService;

    public KeycloakRetryClientTokenProvider(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    @Override
    public String getToken() {
        return keycloakService.getServiceAccessToken();
    }
}
