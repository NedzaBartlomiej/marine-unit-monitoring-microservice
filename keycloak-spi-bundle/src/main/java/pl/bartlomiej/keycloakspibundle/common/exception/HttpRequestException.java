package pl.bartlomiej.keycloakspibundle.common.exception;

public class HttpRequestException extends RuntimeException {
    public HttpRequestException(String requestDetails, Throwable e) {
        super("An unhandled, internal error occurred during the request. Request debug details: " + requestDetails, e);
    }
}
