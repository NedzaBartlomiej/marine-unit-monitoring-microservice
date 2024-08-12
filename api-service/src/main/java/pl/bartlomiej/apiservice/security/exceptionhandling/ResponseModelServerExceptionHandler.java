package pl.bartlomiej.apiservice.security.exceptionhandling;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import pl.bartlomiej.apiservice.common.error.apiexceptions.InvalidJWTException;
import pl.bartlomiej.apiservice.common.error.authexceptions.UnverifiedAccountException;
import pl.bartlomiej.apiservice.common.helper.ResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatusCode.valueOf;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public abstract class ResponseModelServerExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ResponseModelServerExceptionHandler.class);
    private final ObjectMapper objectMapper;

    protected ResponseModelServerExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    protected Mono<Void> processException(final Exception exception, ServerWebExchange exchange) {
        switch (exception) {
            case AccessDeniedException ignoredAccessDeniedException -> {
                return writeExchange(exchange,
                        buildErrorResponse(FORBIDDEN, SecurityError.FORBIDDEN.getMessage()));
            }
            case BadCredentialsException ignoredBadCredentialsException -> {
                return writeExchange(exchange,
                        buildErrorResponse(UNAUTHORIZED, SecurityError.UNAUTHORIZED_CREDENTIALS.getMessage()));
            }
            case LockedException ignoredLockedException -> {
                return writeExchange(exchange,
                        buildErrorResponse(LOCKED, SecurityError.LOCKED.getMessage()));
            }
            case DisabledException ignoredDisabledException -> {
                return writeExchange(exchange,
                        buildErrorResponse(UNAUTHORIZED, UnverifiedAccountException.MESSAGE));
            }
            case UnverifiedAccountException unverifiedAccountException -> {
                return writeExchange(exchange,
                        buildErrorResponse(UNAUTHORIZED, unverifiedAccountException.getMessage()));
            }
            case InvalidJWTException ignoredInvalidJWTException -> {
                return writeExchange(exchange,
                        buildErrorResponse(UNAUTHORIZED, SecurityError.INVALID_TOKEN.getMessage()));
            }
            case AuthenticationException ignoredAuthenticationException -> {
                return writeExchange(exchange,
                        buildErrorResponse(UNAUTHORIZED, SecurityError.UNAUTHORIZED_AUTHENTICATION.getMessage()));
            }
            default -> {
                log.error("Unhandled error message: {}, Exception {}", exception.getMessage(), exception.getClass().getName());
                log.error("Cause: {}, Stack Trace: {}", exception.getCause(), exception.getStackTrace());
                return writeExchange(exchange,
                        buildErrorResponse(INTERNAL_SERVER_ERROR, SecurityError.INTERNAL_ERROR.getMessage()));
            }
        }
    }

    protected ResponseModel<Void> buildErrorResponse(HttpStatus httpStatus, String message) {
        return ResponseModel.<Void>builder()
                .httpStatus(httpStatus)
                .httpStatusCode(httpStatus.value())
                .message(message)
                .build();
    }

    protected Mono<Void> writeExchange(final ServerWebExchange exchange, final ResponseModel<Void> responseModel) {
        exchange.getResponse().setStatusCode(valueOf(responseModel.getHttpStatusCode()));
        exchange.getResponse().setRawStatusCode(responseModel.getHttpStatusCode());
        exchange.getResponse().getHeaders().setContentType(APPLICATION_JSON);

        String jsonResponse;
        try {
            jsonResponse = objectMapper.writeValueAsString(responseModel);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        byte[] bytes = jsonResponse.getBytes(UTF_8);
        Flux<DataBuffer> bufferFlux = Flux.just(
                exchange.getResponse().bufferFactory().wrap(bytes)
        );

        return exchange.getResponse()
                .writeAndFlushWith(Mono.just(bufferFlux));
    }
}