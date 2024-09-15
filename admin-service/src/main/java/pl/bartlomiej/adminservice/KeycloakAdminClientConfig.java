package pl.bartlomiej.adminservice;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakAdminClientConfig {

    @Bean
    Keycloak keycloakAdminClient() {
        return KeycloakBuilder.builder()
                .serverUrl("http://localhost:8080")
                .realm("mum-api-envelope-system-master")
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId("admin-service-server-admin")
                .clientSecret("L5RzPKkeCwdkcVsxLGGuxEoPIioPD0oX")
                .build();
    }

    @Bean
    Keycloak keycloakSuperadminClient() {
        return KeycloakBuilder.builder()
                .serverUrl("http://localhost:8080")
                .realm("mum-api-envelope-system-master")
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId("admin-service-server-superadmin")
                .clientSecret("BkaBljk9xOEBD6k4ZsoLLCYinERVSJJI")
                .build();
    }
}
