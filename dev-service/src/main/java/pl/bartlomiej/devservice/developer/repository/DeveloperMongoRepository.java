package pl.bartlomiej.devservice.developer.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.bartlomiej.devservice.developer.domain.Developer;

public interface DeveloperMongoRepository extends MongoRepository<Developer, String> {
}
