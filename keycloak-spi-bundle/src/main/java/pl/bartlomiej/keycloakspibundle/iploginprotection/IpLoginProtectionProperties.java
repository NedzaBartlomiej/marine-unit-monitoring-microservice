package pl.bartlomiej.keycloakspibundle.iploginprotection;

public record IpLoginProtectionProperties(String tokenUrl, String clientId, String clientSecret,
                                          String protectionServiceUrl) {
}