package pl.bartlomiej.devservice.developer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.bartlomiej.devservice.developer.domain.AppDeveloperEntity;
import pl.bartlomiej.devservice.developer.repository.CustomDeveloperRepository;
import pl.bartlomiej.devservice.developer.repository.DeveloperMongoRepository;
import pl.bartlomiej.mumcommons.keycloakintegration.idm.external.keycloakidm.KeycloakService;
import pl.bartlomiej.mumcommons.keycloakintegration.idm.external.keycloakidm.model.KeycloakUserRepresentation;
import pl.bartlomiej.mumcommons.keycloakintegration.idm.internal.serviceidm.AbstractIDMService;

import java.util.Set;

@Slf4j
@Service
class DefaultDeveloperService extends AbstractIDMService<AppDeveloperEntity> implements DeveloperService {

    private final DeveloperMongoRepository developerMongoRepository;
    private final CustomDeveloperRepository customDeveloperRepository;

    public DefaultDeveloperService(KeycloakService keycloakService, DeveloperMongoRepository developerMongoRepository, CustomDeveloperRepository customDeveloperRepository) {
        super(keycloakService, developerMongoRepository);
        this.developerMongoRepository = developerMongoRepository;
        this.customDeveloperRepository = customDeveloperRepository;
    }

    @Override
    protected AppDeveloperEntity createEntity(final KeycloakUserRepresentation keycloakUserRepresentation, final String ipAddress) {
        return this.createAppDeveloperEntity(keycloakUserRepresentation.id(),
                keycloakUserRepresentation.email(), ipAddress);
    }

    @Override
    protected String getEntityId(AppDeveloperEntity entity) {
        return entity.getId();
    }

    @Override
    public AppDeveloperEntity create(String id, String email, String ipAddress) {
        return this.developerMongoRepository.save(
                this.createAppDeveloperEntity(id, email, ipAddress)
        );
    }

    private AppDeveloperEntity createAppDeveloperEntity(String id, String email, String ipAddress) {
        return new AppDeveloperEntity(id, email, Set.of(ipAddress));
    }

    @Override
    public void trustIp(String id, String ipAddress) {
        log.info("Saving a new trusted IP address for the user with id='{}'", id);
        this.customDeveloperRepository.pushTrustedIpAddress(id, ipAddress);
    }

    @Override
    public boolean verifyIp(String id, String ipAddress) {
        log.info("Verifying if the given IP address is trusted by the user with id='{}'.", id);
        return super.getEntity(id).getTrustedIpAddresses().contains(ipAddress);
    }
}