package pl.bartlomiej.adminservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.bartlomiej.adminservice.domain.AppAdminEntity;

public interface AdminMongoRepository extends MongoRepository<AppAdminEntity, String> {
    Boolean existsByLogin(String login);
}
