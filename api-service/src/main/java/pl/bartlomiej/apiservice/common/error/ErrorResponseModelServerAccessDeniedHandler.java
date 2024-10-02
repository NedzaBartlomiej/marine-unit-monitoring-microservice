package pl.bartlomiej.apiservice.common.error;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import pl.bartlomiej.springexceptionhandlingbundle.external.reactor.ErrorResponseModelServerExceptionHandler;
import reactor.core.publisher.Mono;

@Component
public class ErrorResponseModelServerAccessDeniedHandler implements ServerAccessDeniedHandler {

    private final ErrorResponseModelServerExceptionHandler errorResponseModelServerExceptionHandler;

    public ErrorResponseModelServerAccessDeniedHandler(ErrorResponseModelServerExceptionHandler errorResponseModelServerExceptionHandler) {
        this.errorResponseModelServerExceptionHandler = errorResponseModelServerExceptionHandler;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        return errorResponseModelServerExceptionHandler.processException(exchange, denied);
    }
}