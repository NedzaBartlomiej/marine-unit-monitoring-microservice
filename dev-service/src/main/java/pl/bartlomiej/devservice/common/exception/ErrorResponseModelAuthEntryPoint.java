package pl.bartlomiej.devservice.common.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import pl.bartlomiej.mummicroservicecommons.exceptionhandling.external.servlet.ErrorResponseModelExceptionHandler;

import java.io.IOException;

@Component
public class ErrorResponseModelAuthEntryPoint implements AuthenticationEntryPoint {

    private final ErrorResponseModelExceptionHandler errorResponseModelExceptionHandler;

    public ErrorResponseModelAuthEntryPoint(ErrorResponseModelExceptionHandler errorResponseModelExceptionHandler) {
        this.errorResponseModelExceptionHandler = errorResponseModelExceptionHandler;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        errorResponseModelExceptionHandler.processException(response, authException);
    }
}
