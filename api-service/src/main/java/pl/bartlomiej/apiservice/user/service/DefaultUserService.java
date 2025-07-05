package pl.bartlomiej.apiservice.user.service;

import org.springframework.stereotype.Service;
import pl.bartlomiej.apiservice.user.domain.ApiUserEntity;
import pl.bartlomiej.apiservice.user.repository.MongoUserRepository;
import pl.bartlomiej.mumcommons.globalidmservice.idm.external.keycloakidm.model.KeycloakUserRepresentation;
import pl.bartlomiej.mumcommons.globalidmservice.idm.external.keycloakidm.servlet.KeycloakService;
import pl.bartlomiej.mumcommons.globalidmservice.idm.internal.serviceidm.servlet.AbstractIDMService;

import java.util.Collections;

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
        return this.mongoUserRepository.save(
                this.createApiUserEntity(id, ipAddress)
        );
    }

    private ApiUserEntity createApiUserEntity(String id, String ipAddress) {
        ApiUserEntity apiUserEntity = new ApiUserEntity(id);
        apiUserEntity.setTrustedIpAddresses(Collections.singletonList(ipAddress));
        return apiUserEntity;
    }

    // todo: trustedIpAddresses set instead of list
    @Override
    public void trustIp(String id, String ipAddress) {
        ApiUserEntity user = super.getEntity(id);
        user.getTrustedIpAddresses().add(ipAddress);
        this.mongoUserRepository.save(user);
    }

    @Override
    public boolean verifyIp(String id, String ipAddress) {
        return super.getEntity(id).getTrustedIpAddresses().contains(ipAddress);
    }
}