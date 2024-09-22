package pl.bartlomiej.adminservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.bartlomiej.adminservice.domain.Admin;
import pl.bartlomiej.adminservice.domain.AdminRegisterDto;
import pl.bartlomiej.adminservice.repository.AdminMongoRepository;
import pl.bartlomiej.keycloakidmservice.external.servlet.KeycloakService;

@Slf4j
@Service
public class DefaultAdminService implements AdminService {

    private final KeycloakService keycloakService;
    private final AdminMongoRepository adminMongoRepository;

    public DefaultAdminService(KeycloakService keycloakService, AdminMongoRepository adminMongoRepository) {
        this.keycloakService = keycloakService;
        this.adminMongoRepository = adminMongoRepository;
    }

    @Override
    public Admin create(final AdminRegisterDto adminRegisterDto) {
        log.info("Started user creation process.");
        var keycloakUserRepresentation = keycloakService.create(adminRegisterDto);
        return new Admin(
                keycloakUserRepresentation.id(),
                keycloakUserRepresentation.username()
        );
    }
}