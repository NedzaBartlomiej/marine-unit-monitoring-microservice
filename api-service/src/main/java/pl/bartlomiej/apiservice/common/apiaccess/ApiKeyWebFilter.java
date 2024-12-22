package pl.bartlomiej.apiservice.common.apiaccess;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class ApiKeyWebFilter implements WebFilter {

    private final DevServiceHttpService devServiceHttpService;

    public ApiKeyWebFilter(DevServiceHttpService devServiceHttpService) {
        this.devServiceHttpService = devServiceHttpService;
    }

    @NonNull
    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        return this.extractApiKeyHeader(exchange)
                .flatMap(devServiceHttpService::checkToken)
                .flatMap(responseEntity -> Mono.justOrEmpty(responseEntity.getBody())
                        .flatMap(responseModel -> this.processTokenValidation(responseModel.getBody(), chain, exchange))
                        .switchIfEmpty(Mono.error(new AuthenticationServiceException("Api token validation internal error.")))
                );
    }

    private Mono<String> extractApiKeyHeader(final ServerWebExchange exchange) {
        String xApiKey = exchange.getRequest().getHeaders().getFirst("x-api-key");
        return Mono.justOrEmpty(xApiKey)
                .switchIfEmpty(Mono.error(new AuthenticationCredentialsNotFoundException("x-api-key header is null.")));
    }

    private Mono<Void> processTokenValidation(final boolean isTokenValid, final WebFilterChain chain, final ServerWebExchange exchange) {
        return isTokenValid
                ? chain.filter(exchange)
                : Mono.error(new AuthenticationCredentialsNotFoundException("x-api-key header token is invalid."));
    }

}