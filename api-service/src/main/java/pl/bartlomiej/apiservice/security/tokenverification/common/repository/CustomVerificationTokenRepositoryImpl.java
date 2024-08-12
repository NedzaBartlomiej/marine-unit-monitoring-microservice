package pl.bartlomiej.apiservice.security.tokenverification.common.repository;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pl.bartlomiej.apiservice.security.tokenverification.common.VerificationToken;
import pl.bartlomiej.apiservice.security.tokenverification.common.VerificationTokenConstants;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@Repository
public class CustomVerificationTokenRepositoryImpl implements CustomVerificationTokenRepository {

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public CustomVerificationTokenRepositoryImpl(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @Override
    public Flux<VerificationToken> findExpiredTokens() {
        return reactiveMongoTemplate.find(
                new Query(
                        Criteria.where(VerificationTokenConstants.EXPIRATION).lte(LocalDateTime.now())
                ),
                VerificationToken.class
        );
    }
}
