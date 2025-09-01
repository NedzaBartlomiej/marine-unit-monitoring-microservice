package pl.bartlomiej.apiservice.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import pl.bartlomiej.apiservice.common.exception.apiexception.RecordNotFoundException;
import pl.bartlomiej.apiservice.common.util.CommonShipFields;
import pl.bartlomiej.apiservice.user.domain.ApiUserEntity;
import pl.bartlomiej.apiservice.user.domain.UserConstants;
import pl.bartlomiej.apiservice.user.nested.trackedship.TrackedShip;
import pl.bartlomiej.mumcommons.coreutils.constants.MongoDBCommonConstants;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Slf4j
@Repository
public class CustomUserRepositoryImpl implements CustomUserRepository {

    private final MongoTemplate mongoTemplate;

    public CustomUserRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public TrackedShip pushTrackedShip(String id, TrackedShip trackedShip) {
        this.addToSet(id, UserConstants.TRACKED_SHIPS, trackedShip);
        return trackedShip;
    }

    @Override
    public void pullTrackedShip(String id, String mmsi) {
        mongoTemplate.updateFirst(
                this.getIdValidQuery(id),
                new Update().pull(UserConstants.TRACKED_SHIPS, query(where(CommonShipFields.MMSI).is(mmsi))),
                ApiUserEntity.class
        );
    }

    @Override
    public Set<TrackedShip> getTrackedShips(String id) {
        ApiUserEntity apiUser = mongoTemplate.findById(id, ApiUserEntity.class);
        if (apiUser != null) {
            return Optional.ofNullable(apiUser.getTrackedShips())
                    .orElse(Collections.emptySet());
        } else {
            throw new RecordNotFoundException("User with id: " + id + "not found.");
        }
    }

    @Override
    public void pushTrustedIpAddress(String id, String ipAddress) {
        this.addToSet(id, UserConstants.TRUSTED_IP_ADDRESSES, ipAddress);
    }

    private void addToSet(String id, String updatedFieldName, Object pushedValue) {
        mongoTemplate
                .updateFirst(
                        this.getIdValidQuery(id),
                        new Update().addToSet(updatedFieldName, pushedValue),
                        ApiUserEntity.class
                );
    }

    private Query getIdValidQuery(String id) {
        return new Query(Criteria.where(MongoDBCommonConstants.ID).is(id));
    }
}