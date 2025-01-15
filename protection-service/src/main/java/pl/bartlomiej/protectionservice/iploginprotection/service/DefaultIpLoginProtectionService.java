package pl.bartlomiej.protectionservice.iploginprotection.service;

import org.springframework.stereotype.Service;
import pl.bartlomiej.loginservices.IdmServiceRepresentation;
import pl.bartlomiej.loginservices.IdmServiceResolver;
import pl.bartlomiej.mumcommons.emailintegration.external.EmailHttpService;
import pl.bartlomiej.mumcommons.emailintegration.external.model.LinkedEmail;
import pl.bartlomiej.protectionservice.iploginprotection.controller.IdmServiceHttpService;
import pl.bartlomiej.protectionservice.iploginprotection.model.ProtectionServiceRequest;
import pl.bartlomiej.protectionservice.iploginprotection.suspectlogin.SuspectLogin;
import pl.bartlomiej.protectionservice.iploginprotection.suspectlogin.SuspectLoginService;

@Service
class DefaultIpLoginProtectionService implements IpLoginProtectionService {

    private final IdmServiceResolver idmServiceResolver;
    private final IdmServiceHttpService idmServiceHttpService;
    private final SuspectLoginService suspectLoginService;
    private final EmailHttpService emailHttpService;

    DefaultIpLoginProtectionService(IdmServiceResolver idmServiceResolver,
                                    IdmServiceHttpService idmServiceHttpService,
                                    SuspectLoginService suspectLoginService,
                                    EmailHttpService emailHttpService) {
        this.idmServiceResolver = idmServiceResolver;
        this.idmServiceHttpService = idmServiceHttpService;
        this.suspectLoginService = suspectLoginService;
        this.emailHttpService = emailHttpService;
    }

    /**
     * @return true - when ipAddress from the request param is trusted,
     * and returns false - when the ipAddress is untrusted
     */
    @Override
    public Boolean executeIpLoginProtection(final ProtectionServiceRequest request) {
        IdmServiceRepresentation idmServiceRepresentation = this.idmServiceResolver.resolve(request.clientId());
        boolean isIpTrusted = idmServiceHttpService.verifyIp(
                idmServiceRepresentation.getHostname(),
                idmServiceRepresentation.getPort(),
                idmServiceRepresentation.getResourceApiVersion(),
                idmServiceRepresentation.getIdmResourceIdentifier(),
                request.uid(),
                request.ipAddress()
        ).getBody();
        if (!isIpTrusted) {
            this.executeUntrustedIpAction(request, idmServiceRepresentation);
            return false;
        }
        return true;
    }

    private void executeUntrustedIpAction(final ProtectionServiceRequest request, final IdmServiceRepresentation idmServiceRepresentation) {
        SuspectLogin suspectLogin = this.suspectLoginService.create(request.ipAddress(), request.uid(), idmServiceRepresentation.getClientId());
        this.emailHttpService.sendLinkedEmail(
                new LinkedEmail(
                        request.email(),
                        "Untrusted login activity. 🛑",
                        "We've noticed untrusted log in activity on your account. Please check it.",
                        "http://protection-service/suspect-logins/" + suspectLogin.getId(),
                        "Check activity"
                )
        );
    }

    @Override
    public void trustIp(final String suspectLoginId, final String uid) {
        SuspectLogin suspectLogin = suspectLoginService.get(suspectLoginId, uid);
        IdmServiceRepresentation idmServiceRepresentation = this.idmServiceResolver.resolve(suspectLogin.getIdmServiceClientId());
        idmServiceHttpService.trustIp(
                idmServiceRepresentation.getHostname(),
                idmServiceRepresentation.getPort(),
                idmServiceRepresentation.getResourceApiVersion(),
                idmServiceRepresentation.getIdmResourceIdentifier(),
                suspectLogin.getUid(),
                suspectLogin.getIpAddress()
        );
        suspectLoginService.delete(suspectLoginId);
    }
}