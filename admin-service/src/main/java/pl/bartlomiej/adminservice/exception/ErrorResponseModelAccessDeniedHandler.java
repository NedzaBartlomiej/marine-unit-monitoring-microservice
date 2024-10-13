package pl.bartlomiej.adminservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import pl.bartlomiej.mummicroservicecommons.exceptionhandling.external.servlet.ErrorResponseModelExceptionHandler;

@Component
public class ErrorResponseModelAccessDeniedHandler implements AccessDeniedHandler {

    private final ErrorResponseModelExceptionHandler errorResponseModelExceptionHandler;

    public ErrorResponseModelAccessDeniedHandler(ErrorResponseModelExceptionHandler errorResponseModelExceptionHandler) {
        this.errorResponseModelExceptionHandler = errorResponseModelExceptionHandler;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) {
        errorResponseModelExceptionHandler.processException(response, accessDeniedException);
    }
}
