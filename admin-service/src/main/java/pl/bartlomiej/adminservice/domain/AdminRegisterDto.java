package pl.bartlomiej.adminservice.domain;

import pl.bartlomiej.keycloakidmservice.external.model.KeycloakRole;
import pl.bartlomiej.keycloakidmservice.external.model.KeycloakUserRegistration;

public record AdminRegisterDto(String login, String email, String password) implements KeycloakUserRegistration {
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
