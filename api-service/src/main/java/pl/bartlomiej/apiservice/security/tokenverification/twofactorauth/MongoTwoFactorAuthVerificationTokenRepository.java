package pl.bartlomiej.apiservice.security.tokenverification.twofactorauth;

import pl.bartlomiej.apiservice.security.tokenverification.common.repository.MongoVerificationTokenRepository;
import reactor.core.publisher.Mono;

public interface MongoTwoFactorAuthVerificationTokenRepository extends MongoVerificationTokenRepository<TwoFactorAuthVerificationToken> {
    Mono<TwoFactorAuthVerificationToken> findByCode(String code);
}
