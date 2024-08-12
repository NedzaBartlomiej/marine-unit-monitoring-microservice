package pl.bartlomiej.apiservice.security.tokenverification.resetpassword.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bartlomiej.apiservice.common.error.apiexceptions.AlreadyVerifiedException;
import pl.bartlomiej.apiservice.common.error.apiexceptions.InvalidVerificationTokenException;
import pl.bartlomiej.apiservice.common.error.apiexceptions.NotFoundException;
import pl.bartlomiej.apiservice.emailsending.common.EmailService;
import pl.bartlomiej.apiservice.emailsending.verificationemail.VerificationEmail;
import pl.bartlomiej.apiservice.security.tokenverification.common.VerificationTokenConstants;
import pl.bartlomiej.apiservice.security.tokenverification.common.VerificationTokenType;
import pl.bartlomiej.apiservice.security.tokenverification.common.repository.MongoVerificationTokenRepository;
import pl.bartlomiej.apiservice.security.tokenverification.common.service.AbstractVerificationTokenService;
import pl.bartlomiej.apiservice.security.tokenverification.resetpassword.ResetPasswordVerificationToken;
import pl.bartlomiej.apiservice.security.tokenverification.resetpassword.repository.CustomResetPasswordVerificationTokenRepository;
import pl.bartlomiej.apiservice.user.service.UserService;
import reactor.core.publisher.Mono;

import static reactor.core.publisher.Mono.error;
import static reactor.core.publisher.Mono.just;

@Service
public class ResetPasswordServiceImpl extends AbstractVerificationTokenService<ResetPasswordVerificationToken, Void, Void> implements ResetPasswordService {

    private static final Logger log = LoggerFactory.getLogger(ResetPasswordServiceImpl.class);
    private final UserService userService;
    private final long resetPasswordTokenExpirationTime;
    private final String frontendUrl;
    private final String frontendResetPasswordPath;
    private final MongoVerificationTokenRepository<ResetPasswordVerificationToken> mongoVerificationTokenRepository;
    private final CustomResetPasswordVerificationTokenRepository customVerificationTokenRepository;

    public ResetPasswordServiceImpl(UserService userService,
                                    CustomResetPasswordVerificationTokenRepository customResetPasswordVerificationTokenRepository,
                                    MongoVerificationTokenRepository<ResetPasswordVerificationToken> mongoVerificationTokenRepository,
                                    EmailService<VerificationEmail> emailService,
                                    @Value("${project-properties.expiration-times.verification.reset-password}") long resetPasswordTokenExpirationTime,
                                    @Value("${project-properties.app.frontend-integration.base-url}") String frontendUrl,
                                    @Value("${project-properties.app.frontend-integration.endpoint-paths.reset-password}") String frontendResetPasswordPath) {
        super(emailService, userService, mongoVerificationTokenRepository);
        this.userService = userService;
        this.resetPasswordTokenExpirationTime = resetPasswordTokenExpirationTime;
        this.frontendUrl = frontendUrl;
        this.frontendResetPasswordPath = frontendResetPasswordPath;
        this.mongoVerificationTokenRepository = mongoVerificationTokenRepository;
        this.customVerificationTokenRepository = customResetPasswordVerificationTokenRepository;
    }

    /**
     * @throws NotFoundException when the user is based only on OAuth2 data (when the user isn't created by registration)
     */
    @Transactional(transactionManager = "reactiveTransactionManager")
    @Override
    public Mono<Void> issue(String email, Void carrierObject) {
        return userService.getUserByEmail(email)
                .flatMap(user -> {
                    if (user.getPassword() == null) {
                        return error(NotFoundException::new);
                    }
                    return just(user);
                })
                .flatMap(user -> mongoVerificationTokenRepository.deleteByUid(user.getId())
                        .then(just(user)))
                .flatMap(user -> super.processIssue(
                        user,
                        new ResetPasswordVerificationToken(
                                user.getId(),
                                this.resetPasswordTokenExpirationTime,
                                VerificationTokenType.RESET_PASSWORD_VERIFICATION.name()
                        ),
                        ResetPasswordVerificationToken::getId,
                        VerificationTokenConstants.EMAIL_TITLE_APP_START + "Reset password message."
                ));
    }

    @Override
    public Mono<ResetPasswordVerificationToken> verify(String token) {
        log.info("Verifying reset password token.");
        return super.validateVerificationToken(mongoVerificationTokenRepository.findById(token))
                .flatMap(verificationToken -> verificationToken.getVerified()
                        ? error(AlreadyVerifiedException::new)
                        : just(verificationToken)
                );
    }

    @Override
    public Mono<Void> performVerifiedTokenAction(ResetPasswordVerificationToken verificationToken) {
        log.info("Performing reset password verified token action:");
        return just(verificationToken)
                .flatMap(vt -> customVerificationTokenRepository
                        .updateIsVerified(vt.getId(), true)
                );
    }

    @Override
    protected Mono<Void> sendVerificationToken(String target, String title, String token) {
        return super.sendVerificationEmail(target, title, token, "Reset password");
    }

    @Override
    protected String getVerificationMessage() {
        return "To reset password click this link:";
    }

    @Override
    protected String getVerificationLink(String token) {
        return this.frontendUrl + this.frontendResetPasswordPath + "/" + token;
    }

    @Transactional(transactionManager = "reactiveTransactionManager")
    @Override
    public Mono<Void> processResetPassword(String verificationToken, String newPassword) {
        return super.getVerificationToken(verificationToken)
                .flatMap(vt -> vt.getVerified()
                        ? just(vt)
                        : error(InvalidVerificationTokenException::new)
                )
                .flatMap(vt -> userService.isUserExists(vt.getUid())
                        .then(just(vt)))
                .flatMap(vt -> userService.unlockUser(vt.getUid())
                        .then(just(vt)))
                .flatMap(vt -> userService.updatePassword(vt.getUid(), newPassword)
                        .then(super.deleteVerificationToken(vt.getId()))
                );
    }
}
