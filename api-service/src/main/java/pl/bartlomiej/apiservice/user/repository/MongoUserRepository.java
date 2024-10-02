package pl.bartlomiej.apiservice.user.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pl.bartlomiej.apiservice.user.User;

public interface MongoUserRepository extends ReactiveMongoRepository<User, String> {
}