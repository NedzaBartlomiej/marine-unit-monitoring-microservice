package pl.bartlomiej.protectionservice.iploginprotection.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.bartlomiej.idmservicesreps.IdmServiceRepResolver;
import pl.bartlomiej.idmservicesreps.IdmServiceRepresentation;
import pl.bartlomiej.mumcommons.coreutils.model.response.ResponseModel;
import pl.bartlomiej.mumcommons.emailintegration.external.EmailHttpService;
import pl.bartlomiej.mumcommons.emailintegration.external.model.LinkedEmail;
import pl.bartlomiej.protectionservice.iploginprotection.controller.IdmServiceHttpService;
import pl.bartlomiej.protectionservice.iploginprotection.model.ProtectionServiceRequest;
import pl.bartlomiej.protectionservice.iploginprotection.suspectlogin.SuspectLogin;
import pl.bartlomiej.protectionservice.iploginprotection.suspectlogin.SuspectLoginService;

@Slf4j
@Service
class DefaultIpLoginProtectionService implements IpLoginProtectionService {

    private final IdmServiceRepResolver idmServiceRepResolver;
    private final IdmServiceHttpService idmServiceHttpService;
    private final SuspectLoginService suspectLoginService;
    private final EmailHttpService emailHttpService;

    DefaultIpLoginProtectionService(IdmServiceRepResolver idmServiceRepResolver,
                                    IdmServiceHttpService idmServiceHttpService,
                                    SuspectLoginService suspectLoginService,
                                    EmailHttpService emailHttpService) {
        this.idmServiceRepResolver = idmServiceRepResolver;
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
        log.info("Executing IP login protection for user with id ='{}' which logged to the service which 'loginServiceClientId'='{}'.", request.uid(), request.clientId());
        if (request.uid() == null || request.uid().isBlank() ||
                request.ipAddress() == null || request.ipAddress().isBlank()
        ) {
            throw new IllegalArgumentException("'request.uid()' or 'request.ipAddress()' is null or blank.");
        }
        IdmServiceRepresentation idmServiceRepresentation = this.idmServiceRepResolver.resolve(request.clientId());

        ResponseModel<Boolean> verifiedIpResponse = idmServiceHttpService.verifyIp(
                idmServiceRepresentation.getHostname(),
                idmServiceRepresentation.getPort(),
                idmServiceRepresentation.getResourceApiVersion(),
                idmServiceRepresentation.getIdmResourceIdentifier(),
                request.uid(),
                request.ipAddress()
        );
        boolean isIpTrusted;
        if (verifiedIpResponse == null) {
            log.warn("Received IP address verification is null for the user with id='{}' which logged to the service which 'loginServiceClientId'='{}'; A security alert is sent due to unsuccessful verification.", request.uid(), request.clientId());
            isIpTrusted = false;
        } else if (!verifiedIpResponse.isSuccess()) {
            log.warn("Received an invalid IP address verification response from the IdMService for the user with id='{}' which logged to the service which 'loginServiceClientId'='{}'; response='{}'; A security alert is sent due to unsuccessful verification.", request.uid(), request.clientId(), verifiedIpResponse);
            isIpTrusted = false;
        } else {
            isIpTrusted = verifiedIpResponse.getBody();
        }

        if (!isIpTrusted) {
            log.trace("The IP address is not trusted by the user with id='{}' which logged to the service which 'loginServiceClientId'='{}'.", request.uid(), request.clientId());
            this.executeUntrustedIpAction(request, idmServiceRepresentation);
            return false;
        }
        log.trace("The IP address is trusted by the user with id='{}' which logged to the service which 'loginServiceClientId'='{}'.", request.uid(), request.clientId());
        return true;
    }

    private void executeUntrustedIpAction(final ProtectionServiceRequest request, final IdmServiceRepresentation idmServiceRepresentation) {
        log.trace("Executing untrusted IP address action for the user with id='{}' which logged to the service which 'loginServiceClientId'='{}'.", request.uid(), request.clientId());
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
        log.info("Performing trustIp process for suspect login report with id='{}', for user with id='{}'.", suspectLoginId, uid);
        if (suspectLoginId == null || suspectLoginId.isBlank() ||
                uid == null || uid.isBlank()
        ) throw new IllegalArgumentException("'suspectLoginId' or 'uid' is null or blank.");

        SuspectLogin suspectLogin = suspectLoginService.get(suspectLoginId, uid);
        IdmServiceRepresentation idmServiceRepresentation = this.idmServiceRepResolver.resolve(suspectLogin.getIdmServiceClientId());

        log.trace("Sending 'trustIp' request to the IdmService - '{}', for suspect login report with id='{}', for user with id='{}'.", idmServiceRepresentation.toString(), suspectLoginId, uid);
        idmServiceHttpService.trustIp(
                idmServiceRepresentation.getHostname(),
                idmServiceRepresentation.getPort(),
                idmServiceRepresentation.getResourceApiVersion(),
                idmServiceRepresentation.getIdmResourceIdentifier(),
                suspectLogin.getUid(),
                suspectLogin.getIpAddress()
        );

        log.trace("Deleting the suspect login report with id='{}', for user with id='{}'.", suspectLoginId, uid);
        suspectLoginService.delete(suspectLoginId);
    }
}