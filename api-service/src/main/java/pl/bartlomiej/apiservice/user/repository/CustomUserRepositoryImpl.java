package pl.bartlomiej.apiservice.user.repository;

import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import pl.bartlomiej.apiservice.common.helper.repository.CustomRepository;
import pl.bartlomiej.apiservice.common.util.CommonShipFields;
import pl.bartlomiej.apiservice.user.domain.ApiUserEntity;
import pl.bartlomiej.apiservice.user.domain.UserConstants;
import pl.bartlomiej.apiservice.user.nested.trackedship.TrackedShip;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class CustomUserRepositoryImpl implements CustomUserRepository {

    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final CustomRepository customRepository;

    public CustomUserRepositoryImpl(ReactiveMongoTemplate reactiveMongoTemplate, CustomRepository customRepository) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.customRepository = customRepository;
    }

    @Override
    public Mono<TrackedShip> pushTrackedShip(String id, TrackedShip trackedShip) {
        return this.push(id, UserConstants.TRACKED_SHIPS, trackedShip)
                .then(Mono.just(trackedShip));
    }

    @Override
    public Mono<Void> pullTrackedShip(String id, String mmsi) {
        return reactiveMongoTemplate
                .updateFirst(
                        customRepository.getIdValidQuery(id),
                        new Update().pull(UserConstants.TRACKED_SHIPS, query(where(CommonShipFields.MMSI).is(mmsi))),
                        ApiUserEntity.class
                ).then();
    }

    @Override
    public Mono<Void> pullTrackedShip(String mmsi) {
        return reactiveMongoTemplate
                .updateMulti(
                        new Query(),
                        new Update().pull(UserConstants.TRACKED_SHIPS, query(where(CommonShipFields.MMSI).is(mmsi))),
                        ApiUserEntity.class
                ).then();
    }

    @Override
    public Flux<TrackedShip> getTrackedShips(String id) {
        return reactiveMongoTemplate.findById(id, ApiUserEntity.class)
                .flatMapIterable(ApiUserEntity::getTrackedShips)
                .onErrorResume(NullPointerException.class, ex -> Flux.empty());
    }

    @Override
    public Flux<TrackedShip> getTrackedShips() {
        return reactiveMongoTemplate.findAll(ApiUserEntity.class)
                .flatMapIterable(ApiUserEntity::getTrackedShips)
                .onErrorResume(NullPointerException.class, ex -> Flux.empty());
    }

    private Mono<UpdateResult> push(String id, String updatedFieldName, Object pushedValue) {
        return reactiveMongoTemplate
                .updateFirst(
                        customRepository.getIdValidQuery(id),
                        new Update().push(updatedFieldName, pushedValue),
                        ApiUserEntity.class
                );
    }
}