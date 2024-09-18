package pl.bartlomiej.keycloakidmservice.external;

import pl.bartlomiej.keycloakidmservice.external.model.KeycloakRole;
import pl.bartlomiej.keycloakidmservice.external.model.KeycloakUserRegistration;
import pl.bartlomiej.keycloakidmservice.external.model.KeycloakUserRepresentation;

public interface KeycloakService {
    KeycloakUserRepresentation create(KeycloakUserRegistration keycloakUserRegistration);

    void delete(String id);

    void assignRole(String id, KeycloakRole keycloakRole);
}
