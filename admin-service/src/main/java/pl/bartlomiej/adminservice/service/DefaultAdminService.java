package pl.bartlomiej.adminservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.bartlomiej.adminservice.domain.Admin;
import pl.bartlomiej.adminservice.domain.AdminRegisterDto;
import pl.bartlomiej.adminservice.repository.AdminMongoRepository;
import pl.bartlomiej.adminservice.service.keycloak.DefaultKeycloakService;

@Slf4j
@Service
public class DefaultAdminService implements AdminService {

    private final DefaultKeycloakService defaultKeycloakService;
//    private final AdminMongoRepository adminMongoRepository;

    public DefaultAdminService(DefaultKeycloakService defaultKeycloakService, AdminMongoRepository adminMongoRepository) {
        this.defaultKeycloakService = defaultKeycloakService;
//        this.adminMongoRepository = adminMongoRepository;
    }

    @Override
    public Admin create(final AdminRegisterDto adminRegisterDto) {
        log.info("Started user creation process.");
        Admin admin = defaultKeycloakService.create(adminRegisterDto);
//        if (adminMongoRepository.existsByLogin(admin.getLogin())) {
//            throw new RuntimeException("Admin is already registered.");
//        }
//        adminMongoRepository.save(admin);
        return admin;
    }
}
