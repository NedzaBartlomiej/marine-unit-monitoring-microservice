package pl.bartlomiej.keycloakspibundle.registrationauthenticator;

import com.fasterxml.jackson.databind.JsonNode;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import pl.bartlomiej.keycloakspibundle.common.AuthorizedSimpleHttp;
import pl.bartlomiej.loginservices.IdmServiceResolver;

import java.io.IOException;

public class RegistrationAuthenticator implements Authenticator {

    private final IdmServiceResolver idmServiceResolver;
    private final AuthorizedSimpleHttp authorizedSimpleHttp;

    public RegistrationAuthenticator(IdmServiceResolver idmServiceResolver, AuthorizedSimpleHttp authorizedSimpleHttp) {
        this.idmServiceResolver = idmServiceResolver;
        this.authorizedSimpleHttp = authorizedSimpleHttp;
    }

    @Override
    public void authenticate(AuthenticationFlowContext authenticationFlowContext) {

        // operation data
        UserModel keycloakCreatedUser = authenticationFlowContext.getUser();
        String clientId = authenticationFlowContext.getAuthenticationSession().getClient().getClientId();

        // request data
        var loginServiceRepresentation = this.idmServiceResolver.resolve(clientId);
        String registrationUrl =
                "http://" + loginServiceRepresentation.getHostname()
                        + ":" + loginServiceRepresentation.getPort()
                        + "/" + loginServiceRepresentation.getResourceApiVersion()
                        + "/" + loginServiceRepresentation.getIdmResourceIdentifier();

        // requesting
        KeycloakSession keycloakSession = authenticationFlowContext.getSession();
        SimpleHttp registrationHttp = SimpleHttp.doPost(
                registrationUrl,
                keycloakSession);
        SimpleHttp.Response registrationResponse = this.authorizedSimpleHttp.request(
                registrationHttp,
                new RegistrationServiceRequest(
                        // todo
                ),
                keycloakSession
        );

        // response handling
        try {
            JsonNode json = registrationResponse.asJson();
            boolean success = json.get("success").asBoolean();
            JsonNode createdUser = json.get("body");

            if (!success && createdUser == null) {
                keycloakSession.users().removeUser(
                        keycloakSession.getContext().getRealm(),
                        keycloakCreatedUser
                );
                authenticationFlowContext.failure(AuthenticationFlowError.INTERNAL_ERROR);
                return;
            }
            authenticationFlowContext.success();
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when parsing response to json.", e);
        }
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
}