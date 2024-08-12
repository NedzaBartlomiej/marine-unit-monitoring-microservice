package pl.bartlomiej.apiservice.common.error.apiexceptions;

public class WebClientRequestRetryException extends RuntimeException {
    public WebClientRequestRetryException(String message) {
        super(message);
    }
}