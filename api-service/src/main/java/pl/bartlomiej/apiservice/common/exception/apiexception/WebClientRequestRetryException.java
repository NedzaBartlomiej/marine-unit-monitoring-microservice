package pl.bartlomiej.apiservice.common.exception.apiexception;

public class WebClientRequestRetryException extends RuntimeException {
    public WebClientRequestRetryException(String message) {
        super(message);
    }
}