package pl.bartlomiej.keycloakspibundle.common.exception;

/**
 * An internal protection flow error.
 */
public class ProtectionServiceException extends RuntimeException {
    public ProtectionServiceException(String message) {
        super(message);
    }
}
