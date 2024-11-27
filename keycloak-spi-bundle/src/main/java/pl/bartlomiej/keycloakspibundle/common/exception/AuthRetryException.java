package pl.bartlomiej.keycloakspibundle.common.exception;

public class AuthRetryException extends RuntimeException {
    public AuthRetryException() {
        super("Failed retrying, request is still unauthorized.");
    }
}
