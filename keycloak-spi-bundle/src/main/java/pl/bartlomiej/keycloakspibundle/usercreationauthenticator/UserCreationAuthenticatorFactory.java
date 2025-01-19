package pl.bartlomiej.keycloakspibundle.usercreationauthenticator;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import pl.bartlomiej.idmservicesreps.IdmServiceRepResolver;
import pl.bartlomiej.keycloakspibundle.common.AuthorizedSimpleHttp;
import pl.bartlomiej.keycloakspibundle.common.config.ConfigCache;
import pl.bartlomiej.keycloakspibundle.common.config.ConfigLoader;
import pl.bartlomiej.keycloakspibundle.common.tokenaccess.KeycloakTokenFetcher;
import pl.bartlomiej.keycloakspibundle.common.tokenaccess.KeycloakTokenParams;
import pl.bartlomiej.keycloakspibundle.common.tokenaccess.KeycloakTokenStorage;

import java.util.List;

public class UserCreationAuthenticatorFactory implements AuthenticatorFactory {
    private static final Logger log = LoggerFactory.getLogger(UserCreationAuthenticatorFactory.class);
    private IdmServiceRepResolver idmServiceRepResolver;
    private AuthorizedSimpleHttp authorizedSimpleHttp;

    @Override
    public String getDisplayType() {
        return "Resource Server User Creation";
    }

    @Override
    public String getReferenceCategory() {
        return "";
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return new AuthenticationExecutionModel.Requirement[]{
                AuthenticationExecutionModel.Requirement.REQUIRED
        };
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getHelpText() {
        return "An Authenticator used to create a new user in the resource server's database based on client-id.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return List.of();
    }

    @Override
    public Authenticator create(KeycloakSession keycloakSession) {
        return new UserCreationAuthenticator(this.idmServiceRepResolver, this.authorizedSimpleHttp);
    }

    @Override
    public void init(Config.Scope scope) {
        log.info("UserCreationAuthenticator - init().");
        var yaml = new Yaml();
        var configCache = new ConfigCache();
        var configLoader = new ConfigLoader(yaml, configCache);
        var userCreationAuthenticatorConfig = configLoader.load(
                "user-creation-authenticator-config.yaml",
                UserCreationAuthenticatorConfig.class
        );

        var keycloakTokenParams = new KeycloakTokenParams(
                userCreationAuthenticatorConfig.getTokenUrl(),
                userCreationAuthenticatorConfig.getClientId(),
                userCreationAuthenticatorConfig.getClientSecret()
        );
        var keycloakTokenFetcher = new KeycloakTokenFetcher(keycloakTokenParams);
        var keycloakTokenManager = new KeycloakTokenStorage(keycloakTokenFetcher);
        this.authorizedSimpleHttp = new AuthorizedSimpleHttp(keycloakTokenManager);


        var idmServicesRepresentations = configLoader.load(
                "idm-services-reps-config.yaml",
                IdmServicesRepsConfig.class
        ).getIdmServiceRepresentations();
        this.idmServiceRepResolver = new IdmServiceRepResolver(idmServicesRepresentations);
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return "resource-server-user-creation";
    }
}