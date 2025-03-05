package pl.bartlomiej.apiservice.common.helper.repository;

import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.query.Query;

public interface CustomRepository {

    Query getIdValidQuery(String id);

    UpdateResult updateOne(String id, String updateFieldName, Object updateValue, Class<?> entityClass);

    UpdateResult updateMulti(String id, String updateFieldName, Object updateValue, Class<?> entityClass);
}