package pl.bartlomiej.keycloakspibundle.iploginprotection;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.util.ObjectMapperResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bartlomiej.keycloakspibundle.common.AuthorizedSimpleHttp;
import pl.bartlomiej.keycloakspibundle.common.config.PropertiesProvider;
import pl.bartlomiej.keycloakspibundle.common.tokenaccess.KeycloakTokenFetcher;
import pl.bartlomiej.keycloakspibundle.common.tokenaccess.KeycloakTokenStorage;
import pl.bartlomiej.keycloakspibundle.common.tokenaccess.KeycloakTokenParams;

public class IpLoginEventListenerProviderFactory implements EventListenerProviderFactory {

    private static final Logger log = LoggerFactory.getLogger(IpLoginEventListenerProviderFactory.class);
    private AuthorizedSimpleHttp authorizedSimpleHttp;
    private IpLoginProtectionProperties ipLoginProtectionProperties;

    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        return new IpLoginEventListenerProvider(
                keycloakSession,
                new ProtectionServiceRequestService(
                        keycloakSession,
                        authorizedSimpleHttp,
                        ipLoginProtectionProperties
                )
        );
    }

    @Override
    public void init(Config.Scope scope) {
        log.info("IpLoginEventListenerProviderFactory - init().");
        var propertiesProvider = new PropertiesProvider(ObjectMapperResolver.createStreamSerializer());
        var ipLoginProtectionProperties = propertiesProvider.get(
                "ip-login-protection.properties",
                "",
                IpLoginProtectionProperties.class
        );
        this.ipLoginProtectionProperties = ipLoginProtectionProperties;

        var keycloakTokenParams = new KeycloakTokenParams(
                ipLoginProtectionProperties.tokenUrl(),
                ipLoginProtectionProperties.clientId(),
                ipLoginProtectionProperties.clientSecret()
        );
        var keycloakTokenFetcher = new KeycloakTokenFetcher(keycloakTokenParams);
        var keycloakTokenManager = new KeycloakTokenStorage(keycloakTokenFetcher);
        this.authorizedSimpleHttp = new AuthorizedSimpleHttp(keycloakTokenManager);
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return "ip-login-event-listener-provider";
    }
}
