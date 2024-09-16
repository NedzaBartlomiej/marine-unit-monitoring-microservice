package pl.bartlomiej.adminservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class KeycloakResponseException extends RuntimeException {
    private final HttpStatus httpStatus;

    public KeycloakResponseException(HttpStatus httpStatus) {
        super(httpStatus.name());
        this.httpStatus = httpStatus;
    }

}
