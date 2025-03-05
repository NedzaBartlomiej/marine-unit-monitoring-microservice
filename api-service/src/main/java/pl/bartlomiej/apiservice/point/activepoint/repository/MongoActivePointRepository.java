package pl.bartlomiej.apiservice.point.activepoint.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.bartlomiej.apiservice.point.activepoint.ActivePoint;

public interface MongoActivePointRepository extends MongoRepository<ActivePoint, String> {
}