package pl.bartlomiej.adminservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.bartlomiej.adminservice.domain.Admin;

public interface AdminMongoRepository extends MongoRepository<Admin, String> {
    Boolean existsByLogin(String login);
}
