package pl.bartlomiej.devservice.common.exception;

public class InvalidApplicationRequestStatusException extends RuntimeException {
    public InvalidApplicationRequestStatusException() {
        super("Invalid application request status provided.");
    }
}
