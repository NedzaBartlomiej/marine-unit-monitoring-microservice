package pl.bartlomiej.protectionservice.iploginprotection.suspectlogin;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoSuspectLoginRepository extends MongoRepository<SuspectLogin, String> {
    void deleteAllByTimeBefore(java.util.Date date);
}
