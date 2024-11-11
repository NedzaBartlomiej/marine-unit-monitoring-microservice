package pl.bartlomiej.protectionservice.iploginprotection.service;

import org.springframework.stereotype.Service;
import pl.bartlomiej.mummicroservicecommons.globalidmservice.external.keycloakidm.servlet.KeycloakService;
import pl.bartlomiej.protectionservice.iploginprotection.loginservice.LoginServiceInfoFactory;
import pl.bartlomiej.protectionservice.iploginprotection.model.IpLoginProtectionRequest;

// todo
@Service
class DefaultIpLoginProtectionService implements IpLoginProtectionService {

    private final LoginServiceInfoFactory loginServiceInfoFactory;
    private final KeycloakService keycloakService;

    DefaultIpLoginProtectionService(LoginServiceInfoFactory loginServiceInfoFactory, KeycloakService keycloakService) {
        this.loginServiceInfoFactory = loginServiceInfoFactory;
        this.keycloakService = keycloakService;
    }

    @Override
    public String executeIpLoginProtection(final IpLoginProtectionRequest request) {

    }
}