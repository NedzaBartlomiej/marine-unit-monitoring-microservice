package pl.bartlomiej.adminservice.service.keycloak;

import pl.bartlomiej.adminservice.domain.Admin;
import pl.bartlomiej.adminservice.domain.AdminRegisterDto;

public interface KeycloakService {
    Admin create(AdminRegisterDto adminRegisterDto);

    void delete(String id);

    void assignRole(String id, KeycloakRole keycloakRole);
}
