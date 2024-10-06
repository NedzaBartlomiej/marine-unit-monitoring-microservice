package pl.bartlomiej.adminservice.domain.dto;

import jakarta.validation.constraints.NotBlank;
import pl.bartlomiej.adminservice.domain.AdminKeycloakRole;
import pl.bartlomiej.globalidmservice.external.keycloakidm.model.KeycloakRole;
import pl.bartlomiej.globalidmservice.external.keycloakidm.model.KeycloakUserRegistration;

public record AdminRegisterDto(@NotBlank(message = "EMPTY_LOGIN") String login,
                               @NotBlank(message = "EMPTY_EMAIL") String email,
                               @NotBlank(message = "EMPTY_PASSWORD") String password) implements KeycloakUserRegistration {
    @Override
    public String getUsername() {
        return this.login;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public KeycloakRole getDefaultRole() {
        return AdminKeycloakRole.ADMIN;
    }

    @Override
    public String getEmail() {
        return this.email;
    }
}
