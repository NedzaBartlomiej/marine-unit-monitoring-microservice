package pl.bartlomiej.devservice.developer.domain;

import pl.bartlomiej.mumcommons.globalidmservice.idm.external.keycloakidm.model.KeycloakRole;

public enum DeveloperKeycloakRole implements KeycloakRole {
    DEVELOPER;

    @Override
    public String getRole() {
        return this.name();
    }
}
