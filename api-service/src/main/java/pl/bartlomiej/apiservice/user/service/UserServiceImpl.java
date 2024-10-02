package pl.bartlomiej.apiservice.user.service;

import jakarta.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.bartlomiej.apiservice.user.User;
import pl.bartlomiej.apiservice.user.repository.CustomUserRepository;
import pl.bartlomiej.apiservice.user.repository.MongoUserRepository;
import reactor.core.publisher.Mono;

import static reactor.core.publisher.Mono.*;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final CustomUserRepository customUserRepository;
    private final MongoUserRepository mongoUserRepository;

    public UserServiceImpl(CustomUserRepository customUserRepository,
                           MongoUserRepository mongoUserRepository) {
        this.customUserRepository = customUserRepository;
        this.mongoUserRepository = mongoUserRepository;
    }

    @Override
    public Mono<User> getUser(String id) {
        return mongoUserRepository.findById(id)
                .switchIfEmpty(error(NotFoundException::new));
    }

    @Override
    public Mono<User> createUser(User user, String ipAddress) {
        // todo
        return empty();
    }

    @Override
    public Mono<Void> deleteUser(String id) {
        return this.isUserExists(id)
                .flatMap(exists -> mongoUserRepository.deleteById(id));
    }

    public Mono<Boolean> isUserExists(String id) {
        return mongoUserRepository.existsById(id)
                .flatMap(exists -> exists ? just(true) : error(NotFoundException::new));
    }

    @Override
    public Mono<Void> trustIpAddress(String id, String ipAddress) {
        return customUserRepository.pushTrustedIpAddress(id, ipAddress);
    }
}