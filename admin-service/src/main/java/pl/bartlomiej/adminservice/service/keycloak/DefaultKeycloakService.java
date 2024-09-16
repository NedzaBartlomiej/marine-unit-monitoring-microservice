package pl.bartlomiej.adminservice.service.keycloak;

import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.bartlomiej.adminservice.domain.Admin;
import pl.bartlomiej.adminservice.domain.AdminKeycloakRole;
import pl.bartlomiej.adminservice.domain.AdminRegisterDto;
import pl.bartlomiej.adminservice.exception.KeycloakResponseException;
import pl.bartlomiej.adminservice.exception.OffsetTransactionOperator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class DefaultKeycloakService implements KeycloakService {

    private final RealmResource realmResource;

    public DefaultKeycloakService(@Qualifier("keycloakSuperadminClient") Keycloak keycloak) {
        this.realmResource = keycloak.realm("mum-api-envelope-system-master");
    }

    @Override
    public Admin create(final AdminRegisterDto adminRegisterDto) {
        log.info("Started keycloak user creation process.");
        UserRepresentation userRepresentation = buildUserRepresentation(adminRegisterDto);

        UsersResource usersResource = this.realmResource.users();

        final List<Consumer<Admin>> creationFunctions;
        final List<Consumer<Admin>> creationCompensationFunctions;
        Admin createdAdmin;
        try (Response response = usersResource.create(userRepresentation)) {
            handleResponseStatus(response, HttpStatus.CREATED);

            creationFunctions = new ArrayList<>();
            creationCompensationFunctions = new ArrayList<>();

            // todo - surround with offset transaction
            //  (there is needed to delete user too -
            //  - so creationCompensationFunctions will be used in the comp func)
            String extractedUid = extractIdFromKeycloakLocationHeader(
                    response.getHeaders().getFirst(HttpHeaders.LOCATION)
            );

            createdAdmin = new Admin(
                    extractedUid,
                    adminRegisterDto.login()
            );
        }

        creationFunctions.add(a -> this.assignRole(createdAdmin.getId(), AdminKeycloakRole.ADMIN));
        creationCompensationFunctions.add(a -> this.delete(createdAdmin.getId()));

        OffsetTransactionOperator.performOffsetTransaction(
                createdAdmin,
                createdAdmin,
                creationFunctions,
                creationCompensationFunctions
        );
        return createdAdmin;
    }

    @Override
    public void delete(final String id) {
        log.info("Started deletion keycloak user process.");

        try (Response response = realmResource.users().delete(id)) {
            handleResponseStatus(response, HttpStatus.NO_CONTENT);
        }

        log.info("User has been deleted from the keycloak auth-server.");
    }

    @Override
    public void assignRole(final String id, final KeycloakRole keycloakRole) {
        log.info("Started user assigning role process.");
        UserResource userResource = realmResource.users().get(id);
        RolesResource rolesResource = realmResource.roles();

        log.debug("Getting equivalent keycloak role to argument role.");
        RoleRepresentation roleRepresentation = rolesResource.get(keycloakRole.getRole()).toRepresentation();

        log.info("Assigning role for the admin.");
        userResource.roles().realmLevel().add(Collections.singletonList(roleRepresentation));
    }

    private static void handleResponseStatus(final Response response, final HttpStatus successStatus) {
        if (response.getStatus() != successStatus.value()) {
            log.error("Some error status occurred in keycloak user creation process response: {}" +
                    "Forwarding exception to the RestControllerAdvice", response.getStatusInfo());
            throw new KeycloakResponseException(HttpStatus.valueOf(response.getStatus()));
        }
    }

    private static String extractIdFromKeycloakLocationHeader(final Object header) {

        final Pattern idPattern = Pattern.compile(".*/users/([a-fA-F0-9\\-]{36})");

        if (header == null) {
            throw new IllegalArgumentException("No Location header found.");
        }

        final String headerValue = header.toString();
        log.debug("Extracting keycloak user id from Location header -> {}", headerValue);

        Matcher matcher = idPattern.matcher(headerValue);
        if (!matcher.find()) {
            String foundId = matcher.group(1);
            log.debug("Id found in pattern matching - {}", foundId);
            return foundId;
        } else {
            throw new NoSuchElementException("No Id found in the Location header.");
        }
    }

    private static UserRepresentation buildUserRepresentation(final AdminRegisterDto adminRegisterDto) {
        log.debug("Building UserRepresentation for the user being created.");
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(true);
        userRepresentation.setUsername(adminRegisterDto.login());
        userRepresentation.setEmailVerified(true);

        CredentialRepresentation credentialRepresentation = buildCredentialRepresentation(adminRegisterDto);

        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
        return userRepresentation;
    }

    private static CredentialRepresentation buildCredentialRepresentation(final AdminRegisterDto adminRegisterDto) {
        log.debug("Building CredentialRepresentation for the user being created.");
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setValue(adminRegisterDto.password());
        return credentialRepresentation;
    }
}