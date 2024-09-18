package pl.bartlomiej.keycloakidmservice.external.exception;

import org.springframework.http.HttpStatus;

public class KeycloakResponseException extends RuntimeException {
    private final HttpStatus httpStatus;

    public KeycloakResponseException(HttpStatus httpStatus) {
        super(httpStatus.name());
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}