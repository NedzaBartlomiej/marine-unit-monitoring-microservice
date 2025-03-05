package pl.bartlomiej.apiservice.common.helper.repository;

import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import pl.bartlomiej.apiservice.common.util.CommonFields;

@Repository
public class CustomMongoRepository implements CustomRepository {
    private final MongoTemplate mongoTemplate;

    public CustomMongoRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Query getIdValidQuery(String id) {
        return new Query(Criteria.where(CommonFields.ID).is(id));
    }

    @Override
    public UpdateResult updateOne(String id, String updateFieldName, Object updateValue, Class<?> entityClass) {
        return mongoTemplate.updateFirst(
                this.getIdValidQuery(id),
                this.getFieldUpdate(updateFieldName, updateValue),
                entityClass
        );
    }

    @Override
    public UpdateResult updateMulti(String id, String updateFieldName, Object updateValue, Class<?> entityClass) {
        return mongoTemplate.updateMulti(
                this.getIdValidQuery(id),
                this.getFieldUpdate(updateFieldName, updateValue),
                entityClass
        );
    }

    private Update getFieldUpdate(String updateFieldName, Object updateValue) {
        return new Update().set(updateFieldName, updateValue);
    }
}
