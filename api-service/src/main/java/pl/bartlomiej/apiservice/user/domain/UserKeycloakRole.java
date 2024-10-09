package pl.bartlomiej.apiservice.user.domain;

import pl.bartlomiej.mummicroservicecommons.globalidmservice.external.keycloakidm.model.KeycloakRole;

public enum UserKeycloakRole implements KeycloakRole {
    API_USER("API_USER"), API_PREMIUM_USER("API_PREMIUM_USER"), API_ADMIN("API_ADMIN");

    private final String userRole;

    UserKeycloakRole(String userRole) {
        this.userRole = userRole;
    }

    @Override
    public String getRole() {
        return this.userRole;
    }
}