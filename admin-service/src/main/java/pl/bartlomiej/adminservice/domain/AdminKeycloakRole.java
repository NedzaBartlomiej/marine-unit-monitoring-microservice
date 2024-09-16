package pl.bartlomiej.adminservice.domain;

import pl.bartlomiej.adminservice.service.keycloak.KeycloakRole;

public enum AdminKeycloakRole implements KeycloakRole {
    ADMIN("ADMIN"), SUPERADMIN("SUPERADMIN");

    private final String adminRole;

    AdminKeycloakRole(String adminRole) {
        this.adminRole = adminRole;
    }

    @Override
    public String getRole() {
        return this.adminRole;
    }
}
