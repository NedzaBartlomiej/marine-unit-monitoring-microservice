package pl.bartlomiej.keycloakspibundle.common.tokenaccess;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.models.KeycloakSession;
import pl.bartlomiej.keycloakspibundle.common.config.PropertiesProvider;
import pl.bartlomiej.keycloakspibundle.common.exception.HttpRequestException;
import pl.bartlomiej.keycloakspibundle.iploginprotection.IpLoginProtectionProperties;

import java.io.IOException;

public class TokenFetcher {

    private final IpLoginProtectionProperties ipLoginProtectionProperties;
    private static final Log log = LogFactory.getLog(TokenFetcher.class);

    TokenFetcher(PropertiesProvider propertiesProvider) {
        this.ipLoginProtectionProperties = propertiesProvider.get(
                "ip-login-protection.properties",
                "",
                IpLoginProtectionProperties.class
        );
    }

    String fetchToken(final KeycloakSession keycloakSession) {
        log.info("Fetching token.");
        try {
            return SimpleHttp.doPost(this.ipLoginProtectionProperties.tokenUrl(), keycloakSession)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
                    .param("client_id", this.ipLoginProtectionProperties.clientId())
                    .param("client_secret", this.ipLoginProtectionProperties.clientSecret())
                    .param("grant_type", "client_credentials")
                    .asJson()
                    .get("access_token")
                    .asText();
        } catch (IOException e) {
            throw new HttpRequestException("Access token request.", e);
        }
    }
}