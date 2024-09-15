package pl.bartlomiej.adminservice.service.idm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.bartlomiej.adminservice.domain.Admin;
import pl.bartlomiej.adminservice.domain.AdminRegisterDto;
import pl.bartlomiej.adminservice.repository.AdminMongoRepository;

@Slf4j
@Service
public class IDMAdminServiceImpl implements IDMAdminService {

    private final KeycloakIDMAdminService keycloakAdminService;
//    private final AdminMongoRepository adminMongoRepository;

    public IDMAdminServiceImpl(KeycloakIDMAdminService keycloakAdminService, AdminMongoRepository adminMongoRepository) {
        this.keycloakAdminService = keycloakAdminService;
//        this.adminMongoRepository = adminMongoRepository;
    }

    @Override
    public Admin create(final AdminRegisterDto adminRegisterDto) {
        log.info("Started user creation process.");
        Admin admin = keycloakAdminService.create(adminRegisterDto);
//        if (adminMongoRepository.existsByLogin(admin.getLogin())) {
//            throw new RuntimeException("Admin is already registered.");
//        }
//        adminMongoRepository.save(admin);
        return admin;
    }
}
