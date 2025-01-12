package pl.bartlomiej.protectionservice.iploginprotection.service;

import org.springframework.stereotype.Service;
import pl.bartlomiej.loginservices.LoginServiceRepresentation;
import pl.bartlomiej.loginservices.LoginServiceResolver;
import pl.bartlomiej.mumcommons.emailintegration.external.EmailHttpService;
import pl.bartlomiej.mumcommons.emailintegration.external.model.LinkedEmail;
import pl.bartlomiej.protectionservice.iploginprotection.controller.LoginServiceHttpService;
import pl.bartlomiej.protectionservice.iploginprotection.model.IpLoginProtectionRequest;
import pl.bartlomiej.protectionservice.iploginprotection.suspectlogin.SuspectLogin;
import pl.bartlomiej.protectionservice.iploginprotection.suspectlogin.SuspectLoginService;

@Service
class DefaultIpLoginProtectionService implements IpLoginProtectionService {

    private final LoginServiceResolver loginServiceResolver;
    private final LoginServiceHttpService loginServiceHttpService;
    private final SuspectLoginService suspectLoginService;
    private final EmailHttpService emailHttpService;

    DefaultIpLoginProtectionService(LoginServiceResolver loginServiceResolver,
                                    LoginServiceHttpService loginServiceHttpService,
                                    SuspectLoginService suspectLoginService,
                                    EmailHttpService emailHttpService) {
        this.loginServiceResolver = loginServiceResolver;
        this.loginServiceHttpService = loginServiceHttpService;
        this.suspectLoginService = suspectLoginService;
        this.emailHttpService = emailHttpService;
    }

    /**
     * @return true - when ipAddress from the request param is trusted,
     * and returns false - when the ipAddress is untrusted
     */
    @Override
    public Boolean executeIpLoginProtection(final IpLoginProtectionRequest request) {
        LoginServiceRepresentation loginServiceRepresentation = this.loginServiceResolver.resolve(request.clientId());
        boolean isIpTrusted = loginServiceHttpService.verifyIp(
                loginServiceRepresentation.hostname(),
                loginServiceRepresentation.port(),
                loginServiceRepresentation.loginResourceIdentifier(),
                request.uid(),
                request.ipAddress()
        ).getBody();
        if (!isIpTrusted) {
            this.executeUntrustedIpAction(request, loginServiceRepresentation);
            return false;
        }
        return true;
    }

    private void executeUntrustedIpAction(final IpLoginProtectionRequest request, final LoginServiceRepresentation loginServiceRepresentation) {
        SuspectLogin suspectLogin = this.suspectLoginService.create(request.ipAddress(), request.uid(), loginServiceRepresentation.clientId());
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
        LoginServiceRepresentation loginServiceRepresentation = this.loginServiceResolver.resolve(suspectLogin.getLoginServiceClientId());
        loginServiceHttpService.trustIp(
                loginServiceRepresentation.hostname(),
                loginServiceRepresentation.port(),
                loginServiceRepresentation.loginResourceIdentifier(),
                suspectLogin.getUid(),
                suspectLogin.getIpAddress()
        );
        suspectLoginService.delete(suspectLoginId);
    }
}