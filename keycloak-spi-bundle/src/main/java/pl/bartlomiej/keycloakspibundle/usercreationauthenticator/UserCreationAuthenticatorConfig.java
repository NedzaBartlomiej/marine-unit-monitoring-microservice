package pl.bartlomiej.keycloakspibundle.usercreationauthenticator;

public class UserCreationAuthenticatorConfig {
    private String tokenUrl;
    private String clientId;
    private String clientSecret;

    public UserCreationAuthenticatorConfig() {
    }

    public UserCreationAuthenticatorConfig(String tokenUrl, String clientId, String clientSecret) {
        this.tokenUrl = tokenUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getTokenUrl() {
        return tokenUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setTokenUrl(String tokenUrl) {
        this.tokenUrl = tokenUrl;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
