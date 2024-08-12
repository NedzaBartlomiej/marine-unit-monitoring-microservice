package pl.bartlomiej.apiservice.common.error.apiexceptions;

public class AlreadyVerifiedException extends RuntimeException {
    public AlreadyVerifiedException() {
        super("ALREADY_VERIFIED");
    }
}
