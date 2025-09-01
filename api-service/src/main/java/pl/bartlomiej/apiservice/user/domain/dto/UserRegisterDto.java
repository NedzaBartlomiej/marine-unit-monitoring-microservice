package pl.bartlomiej.apiservice.user.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import pl.bartlomiej.apiservice.user.domain.UserKeycloakRole;
import pl.bartlomiej.mumcommons.keycloakintegration.idm.external.keycloakidm.model.KeycloakRole;
import pl.bartlomiej.mumcommons.keycloakintegration.idm.external.keycloakidm.model.KeycloakUserRegistration;

public record UserRegisterDto(@NotBlank(message = "EMPTY_USERNAME") String username,
                              @Email(message = "INVALID_EMAIL") String email,
                              @NotBlank(message = "EMPTY_PASSWORD") String password) implements KeycloakUserRegistration {

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public KeycloakRole getDefaultRole() {
        return UserKeycloakRole.API_USER;
    }

    @Override
    public String getEmail() {
        return this.email;
    }
}