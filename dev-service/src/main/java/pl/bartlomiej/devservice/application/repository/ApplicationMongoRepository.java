package pl.bartlomiej.devservice.application.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.bartlomiej.devservice.application.domain.Application;

public interface ApplicationMongoRepository extends MongoRepository<Application, String> {
}
