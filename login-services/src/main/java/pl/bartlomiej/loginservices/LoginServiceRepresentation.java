package pl.bartlomiej.loginservices;

public record LoginServiceRepresentation(String hostname, int port, String loginResourceIdentifier, String clientId) {
}
