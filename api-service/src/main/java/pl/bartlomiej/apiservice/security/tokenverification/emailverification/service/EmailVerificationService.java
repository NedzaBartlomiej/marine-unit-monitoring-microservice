package pl.bartlomiej.apiservice.security.tokenverification.emailverification.service;

import pl.bartlomiej.apiservice.security.tokenverification.common.service.VerificationTokenService;
import pl.bartlomiej.apiservice.security.tokenverification.emailverification.EmailVerificationToken;

public interface EmailVerificationService extends VerificationTokenService<EmailVerificationToken, Void, Void> {
}
