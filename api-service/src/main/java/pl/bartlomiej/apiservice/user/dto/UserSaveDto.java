package pl.bartlomiej.apiservice.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import pl.bartlomiej.apiservice.user.UserKeycloakRole;
import pl.bartlomiej.keycloakidmservice.external.model.KeycloakRole;
import pl.bartlomiej.keycloakidmservice.external.model.KeycloakUserRegistration;

public record UserSaveDto(@NotBlank(message = "EMPTY_USERNAME") String username,
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