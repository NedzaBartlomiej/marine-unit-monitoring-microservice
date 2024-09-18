package pl.bartlomiej.keycloakidmservice.external.model;

public interface KeycloakUserRegistration {
    String getUsername();

    String getPassword();

    KeycloakRole getDefaultRole();
}
