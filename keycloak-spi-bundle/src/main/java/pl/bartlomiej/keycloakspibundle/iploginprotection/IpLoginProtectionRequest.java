package pl.bartlomiej.keycloakspibundle.iploginprotection;

public record IpLoginProtectionRequest(String ipAddress, String uid, String email, String clientId) {
}
