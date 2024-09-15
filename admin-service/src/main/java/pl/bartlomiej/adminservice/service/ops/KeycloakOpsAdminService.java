package pl.bartlomiej.adminservice.service.ops;

import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.bartlomiej.adminservice.domain.AdminRole;
import pl.bartlomiej.adminservice.exception.ErrorResponseException;

import java.util.Collections;

@Slf4j
@Service
public class KeycloakOpsAdminService {

    private final RealmResource realmResource;

    public KeycloakOpsAdminService(@Qualifier("keycloakSuperadminClient") Keycloak keycloak) {
        this.realmResource = keycloak.realm("mum-api-envelope-system-master");
    }

    public void assignRole(final String id, final AdminRole adminRole) {
        log.info("Started user assigning role process.");
        UserResource userResource = realmResource.users().get(id);
        RolesResource rolesResource = realmResource.roles();

        log.debug("Getting equivalent keycloak role to argument role.");
        RoleRepresentation roleRepresentation = rolesResource.get(adminRole.name()).toRepresentation();

        log.info("Assigning role for the admin.");
        userResource.roles().realmLevel().add(Collections.singletonList(roleRepresentation));
    }

    // todo refactor (try-catch structure) duplication
    //  (for now this is only with KeycloakIDMAdminService,
    //  but there is possible more in the future)
    public void deleteUser(final String id) {
        log.info("Started deletion keycloak user process.");
        Response response = realmResource.users().delete(id);
        try (response) {
            if (response.getStatus() == HttpStatus.NO_CONTENT.value()) {
                log.info("User has been deleted from the keycloak auth-server.");
            } else {
                throw new ErrorResponseException(HttpStatus.valueOf(response.getStatus()));
            }
        } catch (ErrorResponseException e) {
            log.error("Some error status occurred on deleting keycloak user response: {}, " +
                    "forwarding exception to the RestControllerAdvice", e.getMessage());
            throw new ErrorResponseException(e.getHttpStatus());
        } catch (RuntimeException e) {
            log.error("Some unhandled/server error occurred: ", e);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
