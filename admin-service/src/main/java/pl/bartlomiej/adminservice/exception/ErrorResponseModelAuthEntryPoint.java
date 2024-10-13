package pl.bartlomiej.adminservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import pl.bartlomiej.mummicroservicecommons.exceptionhandling.external.servlet.ErrorResponseModelExceptionHandler;

@Component
public class ErrorResponseModelAuthEntryPoint implements AuthenticationEntryPoint {

    private final ErrorResponseModelExceptionHandler errorResponseModelExceptionHandler;

    public ErrorResponseModelAuthEntryPoint(ErrorResponseModelExceptionHandler errorResponseModelExceptionHandler) {
        this.errorResponseModelExceptionHandler = errorResponseModelExceptionHandler;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        errorResponseModelExceptionHandler.processException(response, authException);
    }
}
