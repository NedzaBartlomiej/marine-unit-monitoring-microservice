package pl.bartlomiej.apiservice.user.domain;

import pl.bartlomiej.mumcommons.keycloakintegration.idm.external.keycloakidm.model.KeycloakRole;

public enum UserKeycloakRole implements KeycloakRole {
    API_USER, API_PREMIUM_USER, API_ADMIN;

    @Override
    public String getRole() {
        return this.name();
    }
}