package pl.bartlomiej.apiservice.common.error.apiexceptions;

import org.springframework.security.core.AuthenticationException;

public class InvalidJWTException extends AuthenticationException {
    public InvalidJWTException() {
        super("Invalid JWT.");
    }
}
