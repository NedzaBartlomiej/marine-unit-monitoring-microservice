package pl.bartlomiej.devservice.application.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.bartlomiej.devservice.application.domain.Application;
import pl.bartlomiej.devservice.application.domain.ApplicationRequestStatus;

import java.util.List;

public interface ApplicationMongoRepository extends MongoRepository<Application, String> {

    Boolean existByAppToken(String appToken);

    Application findByAppToken(String appToken);

    List<Application> findAllByRequestStatus(ApplicationRequestStatus requestStatus);
}
