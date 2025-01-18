package pl.bartlomiej.apiservice.user.service;

import jakarta.ws.rs.NotFoundException;
import org.springframework.stereotype.Service;
import pl.bartlomiej.apiservice.user.domain.ApiUserEntity;
import pl.bartlomiej.apiservice.user.repository.MongoUserRepository;
import pl.bartlomiej.mumcommons.globalidmservice.idm.external.keycloakidm.model.KeycloakUserRepresentation;
import pl.bartlomiej.mumcommons.globalidmservice.idm.external.keycloakidm.reactor.ReactiveKeycloakService;
import pl.bartlomiej.mumcommons.globalidmservice.idm.internal.serviceidm.reactor.AbstractReactiveIDMService;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static reactor.core.publisher.Mono.error;

@Service
class DefaultUserService extends AbstractReactiveIDMService<ApiUserEntity> implements UserService {

    private final MongoUserRepository mongoUserRepository;

    public DefaultUserService(MongoUserRepository mongoUserRepository,
                              ReactiveKeycloakService reactiveKeycloakService) {
        super(reactiveKeycloakService, mongoUserRepository);
        this.mongoUserRepository = mongoUserRepository;
    }

    @Override
    protected ApiUserEntity createEntity(KeycloakUserRepresentation keycloakUserRepresentation, String ipAddress) {
        return createApiUserEntity(keycloakUserRepresentation.id(), ipAddress);
    }

    @Override
    protected String getEntityId(ApiUserEntity entity) {
        return entity.getId();
    }

    @Override
    public Mono<ApiUserEntity> create(String id, String ipAddress) {
        return this.mongoUserRepository.save(
                this.createApiUserEntity(id, ipAddress)
        );
    }

    private ApiUserEntity createApiUserEntity(String id, String ipAddress) {
        ApiUserEntity apiUserEntity = new ApiUserEntity(id);
        apiUserEntity.setTrustedIpAddresses(Collections.singletonList(ipAddress));
        return apiUserEntity;
    }

    @Override
    public Mono<Void> handleUserDoesNotExists(String id) {
        return this.mongoUserRepository.existsById(id)
                .flatMap(exists -> exists ? Mono.empty() : error(NotFoundException::new));
    }

    @Override
    public Mono<Void> trustIp(String id, String ipAddress) {
        return super.getEntity(id)
                .filter(user -> !user.getTrustedIpAddresses().contains(ipAddress))
                .flatMap(user -> {
                    user.getTrustedIpAddresses().add(ipAddress);
                    return this.mongoUserRepository.save(user);
                })
                .then();
    }

    @Override
    public Mono<Boolean> verifyIp(String id, String ipAddress) {
        return super.getEntity(id)
                .map(ApiUserEntity::getTrustedIpAddresses)
                .map(ips -> ips.contains(ipAddress));
    }
}