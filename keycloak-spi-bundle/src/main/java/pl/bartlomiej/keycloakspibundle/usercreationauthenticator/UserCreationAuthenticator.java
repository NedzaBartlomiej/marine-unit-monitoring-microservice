package pl.bartlomiej.keycloakspibundle.usercreationauthenticator;

import com.fasterxml.jackson.databind.JsonNode;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bartlomiej.idmservicesreps.IdmServiceRepResolver;
import pl.bartlomiej.idmservicesreps.IdmServiceRepUserCreationDto;
import pl.bartlomiej.keycloakspibundle.common.delegateprovider.http.HttpDelegateProvider;
import pl.bartlomiej.keycloakspibundle.common.delegateprovider.http.HttpDelegateProviderExecutor;
import pl.bartlomiej.keycloakspibundle.common.delegateprovider.http.MumResponseModelUtil;

public class UserCreationAuthenticator implements Authenticator, HttpDelegateProvider<AuthenticationFlowContext> {

    private static final Logger log = LoggerFactory.getLogger(UserCreationAuthenticator.class);
    private final IdmServiceRepResolver idmServiceRepResolver;
    private final HttpDelegateProviderExecutor<AuthenticationFlowContext> httpDelegateProviderExecutor;

    public UserCreationAuthenticator(IdmServiceRepResolver idmServiceRepResolver, HttpDelegateProviderExecutor<AuthenticationFlowContext> httpDelegateProviderExecutor) {
        this.idmServiceRepResolver = idmServiceRepResolver;
        this.httpDelegateProviderExecutor = httpDelegateProviderExecutor;
    }

    @Override
    public void authenticate(AuthenticationFlowContext authenticationFlowContext) {
        this.httpDelegateProviderExecutor.executeHttpDelegation(
                this,
                authenticationFlowContext,
                authenticationFlowContext.getSession()
        );
    }

    @Override
    public void action(AuthenticationFlowContext authenticationFlowContext) {

    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {

    }

    @Override
    public void close() {

    }

    @Override
    public SimpleHttp buildSimpleHttp(AuthenticationFlowContext context) {
        return SimpleHttp.doPost(
                this.buildCreationUrl(context),
                context.getSession());
    }

    @Override
    public Object buildRequestBody(AuthenticationFlowContext context) {
        UserModel keycloakCreatedUser = context.getUser();

        return new IdmServiceRepUserCreationDto(
                keycloakCreatedUser.getId(),
                keycloakCreatedUser.getEmail(),
                context.getSession().getContext().getConnection().getRemoteAddr()
        );
    }

    @Override
    public void handleSuccess(JsonNode response, AuthenticationFlowContext context) {
        log.info("Successfully created user in the requested resource server: {}", MumResponseModelUtil.getMessage(response));
        context.success();
    }

    @Override
    public void handleFailure(JsonNode response, AuthenticationFlowContext context) {
        log.error("Some error occurred when creating user in the requested resource server: {}", MumResponseModelUtil.getMessage(response));
        UserModel keycloakCreatedUser = context.getUser();
        KeycloakSession keycloakSession = context.getSession();

        keycloakSession.users().removeUser(
                keycloakSession.getContext().getRealm(),
                keycloakCreatedUser
        );
        context.failure(AuthenticationFlowError.INTERNAL_ERROR);
    }

    private String buildCreationUrl(final AuthenticationFlowContext context) {
        String clientId = context.getAuthenticationSession().getClient().getClientId();
        var idmServiceRepresentation = this.idmServiceRepResolver.resolve(clientId);
        return "http://" + idmServiceRepresentation.getHostname()
                + ":" + idmServiceRepresentation.getPort()
                + "/" + idmServiceRepresentation.getResourceApiVersion()
                + "/" + idmServiceRepresentation.getIdmResourceIdentifier();
    }
}