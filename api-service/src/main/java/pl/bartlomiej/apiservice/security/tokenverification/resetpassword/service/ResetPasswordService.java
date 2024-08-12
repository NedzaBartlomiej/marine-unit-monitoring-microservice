package pl.bartlomiej.apiservice.security.tokenverification.resetpassword.service;

import pl.bartlomiej.apiservice.security.tokenverification.common.service.VerificationTokenService;
import pl.bartlomiej.apiservice.security.tokenverification.resetpassword.ResetPasswordVerificationToken;
import reactor.core.publisher.Mono;

public interface ResetPasswordService extends VerificationTokenService<ResetPasswordVerificationToken, Void, Void> {
    Mono<Void> processResetPassword(String verificationToken, String newPassword);
}
