package pl.bartlomiej.keycloakidmservice.internal.implementation;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import pl.bartlomiej.keycloakidmservice.external.KeycloakService;
import pl.bartlomiej.keycloakidmservice.external.exception.KeycloakResponseException;
import pl.bartlomiej.keycloakidmservice.external.model.KeycloakRole;
import pl.bartlomiej.keycloakidmservice.external.model.KeycloakUserRegistration;
import pl.bartlomiej.keycloakidmservice.external.model.KeycloakUserRepresentation;
import pl.bartlomiej.keycloakidmservice.internal.config.KeycloakIDMServiceProperties;
import pl.bartlomiej.offsettransaction.servlet.OffsetTransactionOperator;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultKeycloakService implements KeycloakService {

    private static final Logger log = LoggerFactory.getLogger(DefaultKeycloakService.class);
    private final RealmResource realmResource;

    public DefaultKeycloakService(KeycloakIDMServiceProperties properties, Keycloak keycloak) {
        this.realmResource = keycloak.realm(properties.realmName());
    }

    @Override
    public KeycloakUserRepresentation create(final KeycloakUserRegistration keycloakUserRegistration) {
        log.info("Started keycloak user creation process.");
        UserRepresentation userRepresentation = buildUserRepresentation(keycloakUserRegistration);

        UsersResource usersResource = this.realmResource.users();

        KeycloakUserRepresentation createdUser;
        try (Response response = usersResource.create(userRepresentation)) {
            handleResponseStatus(response, HttpStatus.CREATED);

            String extractedId = OffsetTransactionOperator.performOffsetFunctionTransaction(
                    response.getHeaders().getFirst(HttpHeaders.LOCATION),
                    this.getByUsername(keycloakUserRegistration.getUsername()),
                    DefaultKeycloakService::extractIdFromKeycloakLocationHeader,
                    ur -> this.delete(ur.getId())
            );

            createdUser = new KeycloakUserRepresentation(
                    extractedId,
                    keycloakUserRegistration.getUsername()
            );
        }

        OffsetTransactionOperator.performOffsetConsumerTransaction(
                createdUser,
                createdUser.id(),
                u -> this.assignRole(u.id(), keycloakUserRegistration.getDefaultRole()),
                this::delete
        );

        return createdUser;
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

    private UserRepresentation getByUsername(final String username) {
        log.info("Starting getting keycloak user by username process.");
        List<UserRepresentation> searched = realmResource.users().search(username);
        if (searched.isEmpty()) {
            log.warn("Keycloak user not found by username.");
            throw new NotFoundException();
        } else if (searched.size() > 1) {
            log.error("Searching keycloak users by username method returned more than one user.");
            throw new IllegalStateException();
        } else {
            log.info("Successfully searched unique user by username, returning.");
            return searched.getFirst();
        }
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
        if (matcher.find()) {
            String foundId = matcher.group(1);
            log.debug("Id found in pattern matching - {}", foundId);
            return foundId;
        } else {
            throw new NoSuchElementException("No Id found in the Location header.");
        }
    }

    private static UserRepresentation buildUserRepresentation(final KeycloakUserRegistration keycloakUserRegistration) {
        log.debug("Building UserRepresentation for the user being created.");
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(true);
        userRepresentation.setUsername(keycloakUserRegistration.getUsername());
        userRepresentation.setEmailVerified(true);

        CredentialRepresentation credentialRepresentation = buildCredentialRepresentation(keycloakUserRegistration);

        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
        return userRepresentation;
    }

    private static CredentialRepresentation buildCredentialRepresentation(final KeycloakUserRegistration keycloakUserRegistration) {
        log.debug("Building CredentialRepresentation for the user being created.");
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setValue(keycloakUserRegistration.getPassword());
        return credentialRepresentation;
    }
}