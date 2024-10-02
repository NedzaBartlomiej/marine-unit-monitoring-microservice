package pl.bartlomiej.apiservice.user;

import pl.bartlomiej.keycloakidmservice.external.model.KeycloakRole;

public enum UserKeycloakRole implements KeycloakRole {
    API_USER("API_USER"), API_PREMIUM_USER("API_PREMIUM_USER");

    private final String userRole;

    UserKeycloakRole(String userRole) {
        this.userRole = userRole;
    }

    @Override
    public String getRole() {
        return this.userRole;
    }
}
