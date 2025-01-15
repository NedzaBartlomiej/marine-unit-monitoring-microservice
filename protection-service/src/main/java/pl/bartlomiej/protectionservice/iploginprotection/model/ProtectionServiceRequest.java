package pl.bartlomiej.protectionservice.iploginprotection.model;

public record ProtectionServiceRequest(String ipAddress, String uid, String email, String clientId) {
}
