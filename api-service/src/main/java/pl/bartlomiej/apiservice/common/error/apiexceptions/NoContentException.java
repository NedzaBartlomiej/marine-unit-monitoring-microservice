package pl.bartlomiej.apiservice.common.error.apiexceptions;

public class NoContentException extends RuntimeException {
    public NoContentException() {
        super("NO_CONTENT");
    }
}
