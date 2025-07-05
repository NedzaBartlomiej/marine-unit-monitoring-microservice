package pl.bartlomiej.apiservice.user.repository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import pl.bartlomiej.apiservice.common.exception.apiexception.RecordNotFoundException;
import pl.bartlomiej.apiservice.common.helper.repository.CustomRepository;
import pl.bartlomiej.apiservice.common.util.CommonShipFields;
import pl.bartlomiej.apiservice.user.domain.ApiUserEntity;
import pl.bartlomiej.apiservice.user.domain.UserConstants;
import pl.bartlomiej.apiservice.user.nested.trackedship.TrackedShip;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class CustomUserRepositoryImpl implements CustomUserRepository {

    private final MongoTemplate mongoTemplate;
    private final CustomRepository customRepository;

    public CustomUserRepositoryImpl(MongoTemplate mongoTemplate, CustomRepository customRepository) {
        this.mongoTemplate = mongoTemplate;
        this.customRepository = customRepository;
    }

    @Override
    public TrackedShip pushTrackedShip(String id, TrackedShip trackedShip) {
        boolean pushed = this.push(id, UserConstants.TRACKED_SHIPS, trackedShip);
        if (pushed) return trackedShip;
        else
            throw new RuntimeException("Failed to push tracked ship: " + trackedShip.mmsi() + " to user with id: " + id);
    }

    @Override
    public void pullTrackedShip(String id, String mmsi) {
        mongoTemplate.updateFirst(
                customRepository.getIdValidQuery(id),
                new Update().pull(UserConstants.TRACKED_SHIPS, query(where(CommonShipFields.MMSI).is(mmsi))),
                ApiUserEntity.class
        );
    }

    @Override
    public List<TrackedShip> getTrackedShips(String id) {
        ApiUserEntity apiUser = mongoTemplate.findById(id, ApiUserEntity.class);
        if (apiUser != null) {
            return Optional.ofNullable(apiUser.getTrackedShips())
                    .orElse(Collections.emptyList());
        } else {
            throw new RecordNotFoundException("User with id: " + id + "not found.");
        }
    }

    private boolean push(String id, String updatedFieldName, Object pushedValue) {
        return mongoTemplate
                .updateFirst(
                        customRepository.getIdValidQuery(id),
                        new Update().push(updatedFieldName, pushedValue),
                        ApiUserEntity.class
                ).getModifiedCount() > 0;
    }
}