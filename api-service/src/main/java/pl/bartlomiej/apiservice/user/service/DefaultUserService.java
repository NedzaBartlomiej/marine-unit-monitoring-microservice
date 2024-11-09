package pl.bartlomiej.apiservice.user.service;

import jakarta.ws.rs.NotFoundException;
import org.springframework.stereotype.Service;
import pl.bartlomiej.apiservice.user.domain.User;
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

    private final MongoUserRepository mongoUserRepository;

    public DefaultUserService(MongoUserRepository mongoUserRepository,
                              ReactiveKeycloakService reactiveKeycloakService) {
        super(reactiveKeycloakService, mongoUserRepository);
        this.mongoUserRepository = mongoUserRepository;
    }

    public Mono<Boolean> isUserExists(String id) {
        return mongoUserRepository.existsById(id)
                .flatMap(exists -> exists ? just(true) : error(NotFoundException::new));
    }

    @Override
    public Mono<Void> trustIp(String id, String ipAddress) {
        return super.getEntity(id)
                .filter(user -> !user.getTrustedIpAddresses().contains(ipAddress))
                .flatMap(user -> {
                    user.getTrustedIpAddresses().add(ipAddress);
                    return mongoUserRepository.save(user);
                })
                .then();
    }

    @Override
    public Mono<Boolean> verifyIp(String id, String ipAddress) {
        return super.getEntity(id)
                .map(User::getTrustedIpAddresses)
                .map(ips -> ips.contains(ipAddress));
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