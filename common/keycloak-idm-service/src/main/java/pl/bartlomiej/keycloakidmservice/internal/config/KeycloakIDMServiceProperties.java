package pl.bartlomiej.keycloakidmservice.internal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "keycloak-idm-service")
public record KeycloakIDMServiceProperties(
        Boolean enabled,
        String serverUrl,
        String realmName,
        String clientId,
        String clientSecret
) {
}