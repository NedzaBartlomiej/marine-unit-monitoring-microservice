package pl.bartlomiej.adminservice.service.idm;

import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.bartlomiej.adminservice.domain.Admin;
import pl.bartlomiej.adminservice.domain.AdminRegisterDto;
import pl.bartlomiej.adminservice.domain.AdminRole;
import pl.bartlomiej.adminservice.exception.ErrorResponseException;
import pl.bartlomiej.adminservice.exception.OffsetTransactionOperator;
import pl.bartlomiej.adminservice.service.ops.KeycloakOpsAdminService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class KeycloakIDMAdminService implements IDMAdminService {

    private final KeycloakOpsAdminService keycloakOpsAdminService;
    private final RealmResource realmResource;

    public KeycloakIDMAdminService(KeycloakOpsAdminService keycloakOpsAdminService,
                                   @Qualifier("keycloakSuperadminClient") Keycloak keycloak) {
        this.keycloakOpsAdminService = keycloakOpsAdminService;
        this.realmResource = keycloak.realm("mum-api-envelope-system-master");
    }

    @Override
    public Admin create(final AdminRegisterDto adminRegisterDto) {
        log.info("Started keycloak user creation process.");
        UserRepresentation userRepresentation = buildUserRepresentation(adminRegisterDto);

        UsersResource usersResource = this.realmResource.users();

        Response response = usersResource.create(userRepresentation);
        try (response) {
            if (response.getStatus() == HttpStatus.CREATED.value()) {
                List<Consumer<Admin>> creationFunctions = new ArrayList<>();
                List<Consumer<Admin>> creationCompensationFunctions = new ArrayList<>();

                Admin createdAdmin = new Admin(
                        extractIdFromKeycloakLocationHeader(
                                response.getHeaders().getFirst(HttpHeaders.LOCATION)
                        ),
                        adminRegisterDto.login()
                );

                creationFunctions.add(a -> keycloakOpsAdminService.assignRole(createdAdmin.getId(), AdminRole.ADMIN));
                creationCompensationFunctions.add(a -> keycloakOpsAdminService.deleteUser(createdAdmin.getId()));

                OffsetTransactionOperator.performOffsetTransaction(
                        createdAdmin,
                        creationFunctions,
                        creationCompensationFunctions
                );
                return createdAdmin;
            } else {
                throw new ErrorResponseException(HttpStatus.valueOf(response.getStatus()));
            }
        } catch (ErrorResponseException e) {
            log.error("Some error status occurred in keycloak user creation process response: {}, " +
                    "forwarding exception to the RestControllerAdvice", e.getMessage());
            throw new ErrorResponseException(e.getHttpStatus());
        } catch (RuntimeException e) {
            log.error("Some unhandled/server error occurred: ", e);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static String extractIdFromKeycloakLocationHeader(final Object header) {

        final Pattern idPattern = Pattern.compile(".*/users/([a-fA-F0-9\\-]{36})");

        if (header == null) {
            log.error("No Location header found.");
            throw new IllegalArgumentException("A Location header is required to invoke this operation.");
        }

        final String headerValue = header.toString();
        log.debug("Extracting keycloak user id from Location header -> {}", headerValue);

        Matcher matcher = idPattern.matcher(headerValue);
        if (matcher.find()) {
            String foundId = matcher.group(1);
            log.debug("Id found in pattern matching - {}", foundId);
            return foundId;
        } else {
            log.error("No Id found in the Location header.");
            throw new NoSuchElementException("No ID found in the Location header.");
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