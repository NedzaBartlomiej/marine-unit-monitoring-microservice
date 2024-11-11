package pl.bartlomiej.protectionservice.iploginprotection.model;

public record IpLoginProtectionRequest(String ipAddress, String uid, String email, String clientId) {
}
