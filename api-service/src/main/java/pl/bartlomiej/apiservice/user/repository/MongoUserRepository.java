package pl.bartlomiej.apiservice.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.bartlomiej.apiservice.user.domain.ApiUserEntity;

public interface MongoUserRepository extends MongoRepository<ApiUserEntity, String> {
}