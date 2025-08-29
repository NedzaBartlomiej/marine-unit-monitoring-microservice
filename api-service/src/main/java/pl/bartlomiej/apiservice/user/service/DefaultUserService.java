package pl.bartlomiej.apiservice.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.bartlomiej.apiservice.user.domain.ApiUserEntity;
import pl.bartlomiej.apiservice.user.repository.MongoUserRepository;
import pl.bartlomiej.mumcommons.globalidmservice.idm.external.keycloakidm.KeycloakService;
import pl.bartlomiej.mumcommons.globalidmservice.idm.external.keycloakidm.model.KeycloakUserRepresentation;
import pl.bartlomiej.mumcommons.globalidmservice.idm.internal.serviceidm.AbstractIDMService;

import java.util.Set;

@Slf4j
@Service
class DefaultUserService extends AbstractIDMService<ApiUserEntity> implements UserService {

    private final MongoUserRepository mongoUserRepository;

    public DefaultUserService(MongoUserRepository mongoUserRepository,
                              KeycloakService keycloakService) {
        super(keycloakService, mongoUserRepository);
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
    public ApiUserEntity create(String id, String ipAddress) {
        log.info("Creating user with id='{}'", id);
        return this.mongoUserRepository.save(
                this.createApiUserEntity(id, ipAddress)
        );
    }

    private ApiUserEntity createApiUserEntity(String id, String ipAddress) {
        return new ApiUserEntity(id, null, Set.of(ipAddress));
    }

    // TODO: if add(ipAddress) returns false do not execute the save operation
    @Override
    public void trustIp(String id, String ipAddress) {
        log.info("Saving a new trusted IP address for the user with id='{}'", id);
        ApiUserEntity user = super.getEntity(id);
        user.getTrustedIpAddresses().add(ipAddress);
        this.mongoUserRepository.save(user);
    }

    @Override
    public boolean verifyIp(String id, String ipAddress) {
        log.info("Verifying if the given IP address is trusted by the user with id='{}'.", id);
        return super.getEntity(id).getTrustedIpAddresses().contains(ipAddress);
    }
}