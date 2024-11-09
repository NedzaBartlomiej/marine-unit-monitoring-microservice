package pl.bartlomiej.devservice.developer.service;

import org.springframework.stereotype.Service;
import pl.bartlomiej.devservice.developer.domain.AppDeveloperEntity;
import pl.bartlomiej.devservice.developer.repository.DeveloperMongoRepository;
import pl.bartlomiej.mummicroservicecommons.globalidmservice.external.keycloakidm.model.KeycloakUserRepresentation;
import pl.bartlomiej.mummicroservicecommons.globalidmservice.external.keycloakidm.servlet.KeycloakService;
import pl.bartlomiej.mummicroservicecommons.globalidmservice.internal.serviceidm.servlet.AbstractIDMService;

import java.util.Collections;

@Service
class DefaultDeveloperService extends AbstractIDMService<AppDeveloperEntity> implements DeveloperService {

    private final DeveloperMongoRepository developerMongoRepository;

    public DefaultDeveloperService(KeycloakService keycloakService, DeveloperMongoRepository developerMongoRepository) {
        super(keycloakService, developerMongoRepository);
        this.developerMongoRepository = developerMongoRepository;
    }

    @Override
    protected AppDeveloperEntity createEntity(final KeycloakUserRepresentation keycloakUserRepresentation, final String ipAddress) {
        AppDeveloperEntity appDeveloperEntity = new AppDeveloperEntity(
                keycloakUserRepresentation.id(),
                keycloakUserRepresentation.email()
        );
        appDeveloperEntity.setTrustedIpAddresses(Collections.singletonList(ipAddress));

        return appDeveloperEntity;
    }

    @Override
    protected String getEntityId(AppDeveloperEntity entity) {
        return entity.getId();
    }

    @Override
    public void trustIp(String id, String ipAddress) {
        AppDeveloperEntity developer = super.getEntity(id);
        developer.getTrustedIpAddresses().add(ipAddress);
        developerMongoRepository.save(developer);
    }

    @Override
    public boolean verifyIp(String id, String ipAddress) {
        return super.getEntity(id).getTrustedIpAddresses().contains(ipAddress);
    }
}