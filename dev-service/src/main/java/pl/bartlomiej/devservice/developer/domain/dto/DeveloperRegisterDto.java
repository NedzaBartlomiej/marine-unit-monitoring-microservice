package pl.bartlomiej.devservice.developer.domain.dto;

import jakarta.validation.constraints.NotBlank;
import pl.bartlomiej.devservice.developer.domain.DeveloperKeycloakRole;
import pl.bartlomiej.globalidmservice.external.keycloakidm.model.KeycloakRole;
import pl.bartlomiej.globalidmservice.external.keycloakidm.model.KeycloakUserRegistration;

public record DeveloperRegisterDto(@NotBlank(message = "EMPTY_USERNAME") String username,
                                   @NotBlank(message = "EMPTY_EMAIL") String email,
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
        return DeveloperKeycloakRole.DEVELOPER;
    }

    @Override
    public String getEmail() {
        return this.email;
    }
}
