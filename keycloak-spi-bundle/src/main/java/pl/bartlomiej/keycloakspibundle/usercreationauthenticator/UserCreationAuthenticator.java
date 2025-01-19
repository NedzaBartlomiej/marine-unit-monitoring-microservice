package pl.bartlomiej.keycloakspibundle.usercreationauthenticator;

import com.fasterxml.jackson.databind.JsonNode;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import pl.bartlomiej.idmservicesreps.IdmServiceRepResolver;
import pl.bartlomiej.idmservicesreps.IdmServiceRepUserCreationDto;
import pl.bartlomiej.keycloakspibundle.common.AuthorizedSimpleHttp;

import java.io.IOException;

public class UserCreationAuthenticator implements Authenticator {

    private final IdmServiceRepResolver idmServiceRepResolver;
    private final AuthorizedSimpleHttp authorizedSimpleHttp;

    public UserCreationAuthenticator(IdmServiceRepResolver idmServiceRepResolver, AuthorizedSimpleHttp authorizedSimpleHttp) {
        this.idmServiceRepResolver = idmServiceRepResolver;
        this.authorizedSimpleHttp = authorizedSimpleHttp;
    }

    // todo - refactoring and UAT
    @Override
    public void authenticate(AuthenticationFlowContext authenticationFlowContext) {

        // operation data
        UserModel keycloakCreatedUser = authenticationFlowContext.getUser();
        String clientId = authenticationFlowContext.getAuthenticationSession().getClient().getClientId();
        KeycloakSession keycloakSession = authenticationFlowContext.getSession();

        // request data
        var loginServiceRepresentation = this.idmServiceRepResolver.resolve(clientId);
        String registrationUrl =
                "http://" + loginServiceRepresentation.getHostname()
                        + ":" + loginServiceRepresentation.getPort()
                        + "/" + loginServiceRepresentation.getResourceApiVersion()
                        + "/" + loginServiceRepresentation.getIdmResourceIdentifier();

        // requesting
        SimpleHttp registrationHttp = SimpleHttp.doPost(
                registrationUrl,
                keycloakSession);
        SimpleHttp.Response registrationResponse = this.authorizedSimpleHttp.request(
                registrationHttp,
                new IdmServiceRepUserCreationDto(
                        keycloakCreatedUser.getId(),
                        keycloakCreatedUser.getEmail(),
                        keycloakSession.getContext().getConnection().getRemoteAddr()
                ),
                keycloakSession
        );

        // response handling
        try {
            JsonNode json = registrationResponse.asJson();
            boolean success = json.get("success").asBoolean();
            JsonNode createdUser = json.get("body");

            // registration in idm-service error compensation
            if (!success || createdUser == null) {
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