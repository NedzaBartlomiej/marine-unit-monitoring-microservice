package pl.bartlomiej.adminservice.service;

import org.springframework.stereotype.Service;
import pl.bartlomiej.adminservice.domain.AppAdminEntity;
import pl.bartlomiej.adminservice.repository.AdminMongoRepository;
import pl.bartlomiej.mummicroservicecommons.globalidmservice.external.keycloakidm.model.KeycloakUserRepresentation;
import pl.bartlomiej.mummicroservicecommons.globalidmservice.external.keycloakidm.servlet.KeycloakService;
import pl.bartlomiej.mummicroservicecommons.globalidmservice.internal.serviceidm.servlet.AbstractIDMService;

import java.util.Collections;

@Service
class DefaultAdminService extends AbstractIDMService<AppAdminEntity> implements AdminService {

    public DefaultAdminService(KeycloakService keycloakService, AdminMongoRepository adminMongoRepository) {
        super(keycloakService, adminMongoRepository);
    }


    @Override
    protected AppAdminEntity createEntity(KeycloakUserRepresentation keycloakUserRepresentation, String ipAddress) {
        AppAdminEntity appAdminEntity = new AppAdminEntity(
                keycloakUserRepresentation.id()
        );
        appAdminEntity.setTrustedIpAddresses(Collections.singletonList(ipAddress));
        return appAdminEntity;
    }

    @Override
    protected String getEntityId(AppAdminEntity entity) {
        return entity.getId();
    }
}