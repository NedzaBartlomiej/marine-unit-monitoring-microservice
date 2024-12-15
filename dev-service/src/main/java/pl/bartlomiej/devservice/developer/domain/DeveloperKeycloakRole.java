package pl.bartlomiej.devservice.developer.domain;

import pl.bartlomiej.mumcommons.globalidmservice.idm.external.keycloakidm.model.KeycloakRole;

public enum DeveloperKeycloakRole implements KeycloakRole {
    DEVELOPER("DEVELOPER");

    private final String developerRole;

    DeveloperKeycloakRole(String developerRole) {
        this.developerRole = developerRole;
    }

    @Override
    public String getRole() {
        return this.developerRole;
    }
}
