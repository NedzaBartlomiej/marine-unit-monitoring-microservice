package pl.bartlomiej.devservice.developer.service;

import org.springframework.stereotype.Service;
import pl.bartlomiej.devservice.developer.domain.Developer;
import pl.bartlomiej.devservice.developer.repository.DeveloperMongoRepository;
import pl.bartlomiej.mummicroservicecommons.globalidmservice.external.keycloakidm.model.KeycloakUserRepresentation;
import pl.bartlomiej.mummicroservicecommons.globalidmservice.external.keycloakidm.servlet.KeycloakService;
import pl.bartlomiej.mummicroservicecommons.globalidmservice.internal.serviceidm.servlet.AbstractIDMService;

import java.util.Collections;

@Service
class DefaultDeveloperService extends AbstractIDMService<Developer> implements DeveloperService {

    public DefaultDeveloperService(KeycloakService keycloakService, DeveloperMongoRepository developerMongoRepository) {
        super(keycloakService, developerMongoRepository);
    }

    @Override
    protected Developer createEntity(KeycloakUserRepresentation keycloakUserRepresentation, String ipAddress) {
        Developer developer = new Developer(
                keycloakUserRepresentation.id(),
                keycloakUserRepresentation.username(),
                keycloakUserRepresentation.email()
        );
        developer.setTrustedIpAddresses(Collections.singletonList(ipAddress));

        return developer;
    }

    @Override
    protected String getEntityId(Developer entity) {
        return entity.getId();
    }
}