package pl.bartlomiej.adminservice.domain;

import pl.bartlomiej.mummicroservicecommons.globalidmservice.external.keycloakidm.model.KeycloakRole;

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