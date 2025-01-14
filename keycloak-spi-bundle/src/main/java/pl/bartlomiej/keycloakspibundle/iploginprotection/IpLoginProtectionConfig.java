package pl.bartlomiej.keycloakspibundle.iploginprotection;

public class IpLoginProtectionConfig {
    private String tokenUrl;
    private String clientId;
    private String clientSecret;
    private String protectionServiceUrl;

    public IpLoginProtectionConfig() {
    }

    public IpLoginProtectionConfig(String tokenUrl, String clientId, String clientSecret, String protectionServiceUrl) {
        this.tokenUrl = tokenUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.protectionServiceUrl = protectionServiceUrl;
    }

    public String getTokenUrl() {
        return tokenUrl;
    }

    public void setTokenUrl(String tokenUrl) {
        this.tokenUrl = tokenUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getProtectionServiceUrl() {
        return protectionServiceUrl;
    }

    public void setProtectionServiceUrl(String protectionServiceUrl) {
        this.protectionServiceUrl = protectionServiceUrl;
    }
}