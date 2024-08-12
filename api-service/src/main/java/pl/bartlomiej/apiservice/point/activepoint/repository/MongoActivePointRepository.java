package pl.bartlomiej.apiservice.point.activepoint.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pl.bartlomiej.apiservice.point.activepoint.ActivePoint;

public interface MongoActivePointRepository extends ReactiveMongoRepository<ActivePoint, String> {
}