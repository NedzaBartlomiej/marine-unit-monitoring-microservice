package pl.bartlomiej.devservice.developer.repository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import pl.bartlomiej.devservice.developer.domain.AppDeveloperEntity;
import pl.bartlomiej.devservice.developer.domain.DeveloperConstants;
import pl.bartlomiej.mumcommons.coreutils.constants.MongoDBCommonConstants;

@Repository
public class CustomDeveloperRepositoryImpl implements CustomDeveloperRepository {

    private final MongoTemplate mongoTemplate;

    public CustomDeveloperRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void pushTrustedIpAddress(String id, String ipAddress) {
        mongoTemplate
                .updateFirst(
                        new Query(Criteria.where(MongoDBCommonConstants.ID).is(id)),
                        new Update().addToSet(DeveloperConstants.TRUSTED_IP_ADDRESSES, ipAddress),
                        AppDeveloperEntity.class
                );
    }
}
