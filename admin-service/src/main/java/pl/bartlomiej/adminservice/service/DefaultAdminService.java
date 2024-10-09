package pl.bartlomiej.adminservice.service;

import org.springframework.stereotype.Service;
import pl.bartlomiej.adminservice.domain.Admin;
import pl.bartlomiej.adminservice.repository.AdminMongoRepository;
import pl.bartlomiej.mummicroservicecommons.globalidmservice.external.keycloakidm.model.KeycloakUserRepresentation;
import pl.bartlomiej.mummicroservicecommons.globalidmservice.external.keycloakidm.servlet.KeycloakService;
import pl.bartlomiej.mummicroservicecommons.globalidmservice.internal.serviceidm.servlet.AbstractIDMService;

import java.util.Collections;

@Service
class DefaultAdminService extends AbstractIDMService<Admin> implements AdminService {

    public DefaultAdminService(KeycloakService keycloakService, AdminMongoRepository adminMongoRepository) {
        super(keycloakService, adminMongoRepository);
    }


    @Override
    protected Admin createEntity(KeycloakUserRepresentation keycloakUserRepresentation, String ipAddress) {
        Admin admin = new Admin(
                keycloakUserRepresentation.id(),
                keycloakUserRepresentation.username(),
                keycloakUserRepresentation.email()
        );
        admin.setTrustedIpAddresses(Collections.singletonList(ipAddress));
        return admin;
    }

    @Override
    protected String getEntityId(Admin entity) {
        return entity.getId();
    }
}