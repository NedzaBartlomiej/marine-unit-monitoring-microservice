package pl.bartlomiej.apiservice.user.service;

import jakarta.ws.rs.NotFoundException;
import org.springframework.stereotype.Service;
import pl.bartlomiej.apiservice.user.domain.User;
import pl.bartlomiej.apiservice.user.repository.CustomUserRepository;
import pl.bartlomiej.apiservice.user.repository.MongoUserRepository;
import pl.bartlomiej.mummicroservicecommons.globalidmservice.external.keycloakidm.model.KeycloakUserRepresentation;
import pl.bartlomiej.mummicroservicecommons.globalidmservice.external.keycloakidm.reactor.ReactiveKeycloakService;
import pl.bartlomiej.mummicroservicecommons.globalidmservice.internal.serviceidm.reactor.AbstractReactiveIDMService;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static reactor.core.publisher.Mono.error;
import static reactor.core.publisher.Mono.just;

@Service
class DefaultUserService extends AbstractReactiveIDMService<User> implements UserService {

    private final CustomUserRepository customUserRepository;
    private final MongoUserRepository mongoUserRepository;

    public DefaultUserService(CustomUserRepository customUserRepository,
                              MongoUserRepository mongoUserRepository,
                              ReactiveKeycloakService reactiveKeycloakService) {
        super(reactiveKeycloakService, mongoUserRepository);
        this.customUserRepository = customUserRepository;
        this.mongoUserRepository = mongoUserRepository;
    }

    @Override
    public Mono<User> getUser(String id) {
        return mongoUserRepository.findById(id)
                .switchIfEmpty(error(NotFoundException::new));
    }

    public Mono<Boolean> isUserExists(String id) {
        return mongoUserRepository.existsById(id)
                .flatMap(exists -> exists ? just(true) : error(NotFoundException::new));
    }

    @Override
    public Mono<Void> trustIpAddress(String id, String ipAddress) {
        return customUserRepository.pushTrustedIpAddress(id, ipAddress);
    }

    @Override
    protected User createEntity(KeycloakUserRepresentation keycloakUserRepresentation, String ipAddress) {
        User user = new User(
                keycloakUserRepresentation.id()
        );
        user.setTrustedIpAddresses(Collections.singletonList(ipAddress));

        return user;
    }

    @Override
    protected String getEntityId(User entity) {
        return entity.getId();
    }
}