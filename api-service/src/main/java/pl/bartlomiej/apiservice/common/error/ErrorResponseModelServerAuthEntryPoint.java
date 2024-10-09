package pl.bartlomiej.apiservice.common.error;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import pl.bartlomiej.mummicroservicecommons.exceptionhandling.external.reactor.ErrorResponseModelServerExceptionHandler;
import reactor.core.publisher.Mono;

@Component
public class ErrorResponseModelServerAuthEntryPoint implements ServerAuthenticationEntryPoint {

    private final ErrorResponseModelServerExceptionHandler errorResponseModelServerExceptionHandler;

    public ErrorResponseModelServerAuthEntryPoint(ErrorResponseModelServerExceptionHandler errorResponseModelServerExceptionHandler) {
        this.errorResponseModelServerExceptionHandler = errorResponseModelServerExceptionHandler;
    }

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        return errorResponseModelServerExceptionHandler.processException(exchange, ex);
    }
}
