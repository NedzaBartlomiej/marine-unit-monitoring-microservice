package pl.bartlomiej.loginservices;

// todo - to be used by snakeYaml it needs to be a Class
public record LoginServiceRepresentation(String hostname, int port, String loginResourceIdentifier, String clientId) {
}
