package pl.bartlomiej.apiservice.user.service;

import jakarta.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.bartlomiej.apiservice.user.User;
import pl.bartlomiej.apiservice.user.dto.UserSaveDto;
import pl.bartlomiej.apiservice.user.repository.CustomUserRepository;
import pl.bartlomiej.apiservice.user.repository.MongoUserRepository;
import pl.bartlomiej.keycloakidmservice.external.reactor.ReactiveKeycloakService;
import pl.bartlomiej.offsettransaction.reactor.ReactiveOffsetTransactionOperator;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static reactor.core.publisher.Mono.error;
import static reactor.core.publisher.Mono.just;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final CustomUserRepository customUserRepository;
    private final MongoUserRepository mongoUserRepository;
    private final ReactiveKeycloakService keycloakService;

    public UserServiceImpl(CustomUserRepository customUserRepository,
                           MongoUserRepository mongoUserRepository, ReactiveKeycloakService keycloakService) {
        this.customUserRepository = customUserRepository;
        this.mongoUserRepository = mongoUserRepository;
        this.keycloakService = keycloakService;
    }

    @Override
    public Mono<User> getUser(String id) {
        return mongoUserRepository.findById(id)
                .switchIfEmpty(error(NotFoundException::new));
    }

    @Override
    public Mono<User> createUser(UserSaveDto userSaveDto, String ipAddress) {
        return keycloakService.create(userSaveDto)
                .flatMap(keycloakUserRepresentation -> {
                    User user = new User(keycloakUserRepresentation.id(),
                            keycloakUserRepresentation.username(),
                            keycloakUserRepresentation.email()
                    );
                    user.setTrustedIpAddresses(Collections.singletonList(ipAddress));

                    return ReactiveOffsetTransactionOperator.performOffsetFunctionTransaction(
                            user,
                            user.getId(),
                            mongoUserRepository::save,
                            keycloakService::delete
                    );
                });
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