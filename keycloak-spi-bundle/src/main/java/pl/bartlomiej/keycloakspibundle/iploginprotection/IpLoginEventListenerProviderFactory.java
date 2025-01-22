package pl.bartlomiej.keycloakspibundle.iploginprotection;

import org.keycloak.Config;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import pl.bartlomiej.keycloakspibundle.common.AuthorizedSimpleHttp;
import pl.bartlomiej.keycloakspibundle.common.config.ConfigCache;
import pl.bartlomiej.keycloakspibundle.common.config.ConfigLoader;
import pl.bartlomiej.keycloakspibundle.common.delegateprovider.http.HttpDelegateProviderExecutor;
import pl.bartlomiej.keycloakspibundle.common.delegateprovider.http.HttpDelegateService;
import pl.bartlomiej.keycloakspibundle.common.tokenaccess.KeycloakTokenFetcher;
import pl.bartlomiej.keycloakspibundle.common.tokenaccess.KeycloakTokenParams;
import pl.bartlomiej.keycloakspibundle.common.tokenaccess.KeycloakTokenStorage;

public class IpLoginEventListenerProviderFactory implements EventListenerProviderFactory {

    private static final Logger log = LoggerFactory.getLogger(IpLoginEventListenerProviderFactory.class);
    private IpLoginProtectionConfig ipLoginProtectionConfig;
    private HttpDelegateProviderExecutor<Event> httpDelegateProviderExecutor;

    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        return new IpLoginEventListenerProvider(
                keycloakSession,
                this.ipLoginProtectionConfig,
                this.httpDelegateProviderExecutor
        );
    }

    @Override
    public void init(Config.Scope scope) {
        log.info("IpLoginEventListenerProviderFactory - init().");

        var yaml = new Yaml();
        var configCache = new ConfigCache();
        var configLoader = new ConfigLoader(yaml, configCache);
        var ipLoginProtectionProperties = configLoader.load(
                "ip-login-protection-config.yaml",
                IpLoginProtectionConfig.class
        );
        this.ipLoginProtectionConfig = ipLoginProtectionProperties;

        var delegateHttpService = getHttpDelegateService(ipLoginProtectionProperties);
        this.httpDelegateProviderExecutor = new HttpDelegateProviderExecutor<>(delegateHttpService);
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

    private static HttpDelegateService getHttpDelegateService(IpLoginProtectionConfig ipLoginProtectionProperties) {
        var keycloakTokenParams = new KeycloakTokenParams(
                ipLoginProtectionProperties.getTokenUrl(),
                ipLoginProtectionProperties.getClientId(),
                ipLoginProtectionProperties.getClientSecret()
        );
        var keycloakTokenFetcher = new KeycloakTokenFetcher(keycloakTokenParams);
        var keycloakTokenManager = new KeycloakTokenStorage(keycloakTokenFetcher);

        var authorizedSimpleHttp = new AuthorizedSimpleHttp(keycloakTokenManager);

        return new HttpDelegateService(authorizedSimpleHttp);
    }
}