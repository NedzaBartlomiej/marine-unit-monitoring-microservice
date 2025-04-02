package pl.bartlomiej.apiservice.common.exception.apiexception;

public class RecordNotFoundException extends RuntimeException {
    public RecordNotFoundException(String message) {
        super(message);
    }
}
