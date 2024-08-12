package pl.bartlomiej.apiservice.security.tokenverification.ipauthprotection.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bartlomiej.apiservice.emailsending.common.EmailService;
import pl.bartlomiej.apiservice.emailsending.verificationemail.VerificationEmail;
import pl.bartlomiej.apiservice.security.authentication.jwt.service.JWTService;
import pl.bartlomiej.apiservice.security.tokenverification.common.VerificationTokenConstants;
import pl.bartlomiej.apiservice.security.tokenverification.common.VerificationTokenType;
import pl.bartlomiej.apiservice.security.tokenverification.common.repository.MongoVerificationTokenRepository;
import pl.bartlomiej.apiservice.security.tokenverification.common.service.AbstractVerificationTokenService;
import pl.bartlomiej.apiservice.security.tokenverification.ipauthprotection.IpAuthProtectionVerificationToken;
import pl.bartlomiej.apiservice.user.User;
import pl.bartlomiej.apiservice.user.service.UserService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class IpAuthProtectionServiceImpl extends AbstractVerificationTokenService<IpAuthProtectionVerificationToken, String, Void> implements IpAuthProtectionService {

    private static final Logger log = LoggerFactory.getLogger(IpAuthProtectionServiceImpl.class);
    private final String frontendUrl;
    private final long ipAuthProtectionTokenExpirationTime;
    private final UserService userService;
    private final MongoVerificationTokenRepository<IpAuthProtectionVerificationToken> mongoVerificationTokenRepository;
    private final String frontendUntrustedAuthenticationPath;
    private final JWTService jwtService;

    public IpAuthProtectionServiceImpl(EmailService<VerificationEmail> emailService,
                                       MongoVerificationTokenRepository<IpAuthProtectionVerificationToken> mongoVerificationTokenRepository,
                                       UserService userService,
                                       @Value("${project-properties.app.frontend-integration.base-url}") String frontendUrl,
                                       @Value("${project-properties.expiration-times.verification.ip-address-token}") long ipAuthProtectionTokenExpirationTime,
                                       @Value("${project-properties.app.frontend-integration.endpoint-paths.untrusted-authentication}") String frontendUntrustedAuthenticationPath,
                                       JWTService jwtService) {
        super(emailService, userService, mongoVerificationTokenRepository);
        this.frontendUrl = frontendUrl;
        this.userService = userService;
        this.mongoVerificationTokenRepository = mongoVerificationTokenRepository;
        this.ipAuthProtectionTokenExpirationTime = ipAuthProtectionTokenExpirationTime;
        this.frontendUntrustedAuthenticationPath = frontendUntrustedAuthenticationPath;
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> issue(String uid, String ipAddress) {
        return userService.getUser(uid)
                .flatMap(user -> super.processIssue(
                        user,
                        new IpAuthProtectionVerificationToken(
                                user.getId(),
                                this.ipAuthProtectionTokenExpirationTime,
                                VerificationTokenType.TRUSTED_IP_ADDRESS_VERIFICATION.name(),
                                ipAddress
                        ),
                        IpAuthProtectionVerificationToken::getId,
                        VerificationTokenConstants.EMAIL_TITLE_APP_START + "New untrusted account authentication detected."
                ));
    }

    @Override
    public Mono<IpAuthProtectionVerificationToken> verify(String token) {
        log.info("Verifying IP auth protection verification token.");
        return super.validateVerificationToken(mongoVerificationTokenRepository.findById(token));
    }

    @Override
    protected Mono<Void> sendVerificationToken(String target, String title, String token) {
        return super.sendVerificationEmail(target, title, token, "Check");
    }

    @Override
    protected String getVerificationMessage() {
        return "We have detected untrusted authentication activity on your account, please check it:";
    }

    @Override
    protected String getVerificationLink(String token) {
        return frontendUrl + frontendUntrustedAuthenticationPath + "/" + token;
    }

    @Transactional(transactionManager = "reactiveTransactionManager")
    @Override
    public Mono<Void> trustIpAddress(IpAuthProtectionVerificationToken verificationToken) {
        log.info("Adding new trusted ip address for user.");
        return userService.trustIpAddress(verificationToken.getUid(), verificationToken.getIpAddress())
                .then(mongoVerificationTokenRepository.delete(verificationToken));
    }

    @Transactional(transactionManager = "reactiveTransactionManager")
    @Override
    public Mono<Void> blockAccount(IpAuthProtectionVerificationToken verificationToken) {
        log.info("Blocking user account after untrusted activity confirmation.");
        return userService.blockUser(verificationToken.getUid())
                .then(jwtService.invalidateAll(verificationToken.getUid()))
                .then(mongoVerificationTokenRepository.delete(verificationToken));
    }

    @Override
    public Mono<Void> processProtection(String uid, String ipAddress) {
        log.info("Processing trusted IP authentication protection.");
        return userService.getUser(uid)
                .map(User::getTrustedIpAddresses)
                .flatMapMany(Flux::fromIterable)
                .filter(ip -> ip.equals(ipAddress))
                .hasElement(ipAddress)
                .flatMap(isTrusted -> {
                    if (isTrusted) {
                        log.info("Trusted IP, continues the flow.");
                        return Mono.empty();
                    }
                    log.info("Untrusted IP, performing untrusted IP action.");
                    return this.issue(uid, ipAddress);
                });
    }
}
