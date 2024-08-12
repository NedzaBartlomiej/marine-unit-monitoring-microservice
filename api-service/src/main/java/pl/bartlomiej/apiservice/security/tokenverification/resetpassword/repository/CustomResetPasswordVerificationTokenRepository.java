package pl.bartlomiej.apiservice.security.tokenverification.resetpassword.repository;

import reactor.core.publisher.Mono;

public interface CustomResetPasswordVerificationTokenRepository {
    Mono<Void> updateIsVerified(String id, boolean isVerified);
}
