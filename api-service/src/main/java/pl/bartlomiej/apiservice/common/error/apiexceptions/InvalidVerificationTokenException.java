package pl.bartlomiej.apiservice.common.error.apiexceptions;

public class InvalidVerificationTokenException extends RuntimeException {
    public InvalidVerificationTokenException() {
        super("INVALID_VERIFICATION_TOKEN");
    }
}
