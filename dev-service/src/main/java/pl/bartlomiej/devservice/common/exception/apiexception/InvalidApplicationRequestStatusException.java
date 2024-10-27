package pl.bartlomiej.devservice.common.exception.apiexception;

public class InvalidApplicationRequestStatusException extends RuntimeException {
    public InvalidApplicationRequestStatusException() {
        super("Invalid application request status provided.");
    }
}
