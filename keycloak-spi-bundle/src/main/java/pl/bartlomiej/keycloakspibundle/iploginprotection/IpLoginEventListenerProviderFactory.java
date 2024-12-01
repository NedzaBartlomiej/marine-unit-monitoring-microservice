package pl.bartlomiej.keycloakspibundle.iploginprotection;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.util.ObjectMapperResolver;
import pl.bartlomiej.keycloakspibundle.common.AuthorizedSimpleHttp;
import pl.bartlomiej.keycloakspibundle.common.config.PropertiesProvider;

public class IpLoginEventListenerProviderFactory implements EventListenerProviderFactory {

    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        return new IpLoginEventListenerProvider(keycloakSession,
                new ProtectionServiceRequestService(
                        keycloakSession, new AuthorizedSimpleHttp(),
                        new PropertiesProvider(ObjectMapperResolver.createStreamSerializer())
                )
        );
    }

    @Override
    public void init(Config.Scope scope) {

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
