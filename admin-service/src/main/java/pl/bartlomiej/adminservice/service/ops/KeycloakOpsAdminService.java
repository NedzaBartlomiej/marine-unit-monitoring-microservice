package pl.bartlomiej.adminservice.service.ops;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.bartlomiej.adminservice.domain.AdminRole;

import java.util.Collections;

@Slf4j
@Service
public class KeycloakOpsAdminService {

    private final RealmResource realmResource;

    public KeycloakOpsAdminService(@Qualifier("keycloakSuperadminClient") Keycloak keycloak) {
        this.realmResource = keycloak.realm("mum-api-envelope-system-master");
    }

    public void assignRole(final String aid, final AdminRole adminRole) {
        UserResource userResource = realmResource.users().get(aid);
        RolesResource rolesResource = realmResource.roles();

        log.debug("Getting equivalent keycloak role to argument role.");
        RoleRepresentation roleRepresentation = rolesResource.get(adminRole.name()).toRepresentation();

        log.info("Assigning role for the admin.");
        userResource.roles().realmLevel().add(Collections.singletonList(roleRepresentation));
    }
}
