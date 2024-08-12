package pl.bartlomiej.apiservice.security.tokenverification.twofactorauth.service;

import pl.bartlomiej.apiservice.security.tokenverification.common.service.VerificationTokenService;
import pl.bartlomiej.apiservice.security.tokenverification.twofactorauth.TwoFactorAuthVerificationToken;

import java.util.Map;

public interface TwoFactorAuthService extends VerificationTokenService<TwoFactorAuthVerificationToken, Void, Map<String, String>> {
}
