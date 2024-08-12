package pl.bartlomiej.apiservice.security.tokenverification.common.repository;

import pl.bartlomiej.apiservice.security.tokenverification.common.VerificationToken;
import reactor.core.publisher.Flux;

public interface CustomVerificationTokenRepository {
    Flux<VerificationToken> findExpiredTokens();
}
