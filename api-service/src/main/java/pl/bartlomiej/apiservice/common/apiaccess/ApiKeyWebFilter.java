package pl.bartlomiej.apiservice.common.apiaccess;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import pl.bartlomiej.mumcommons.core.constants.TokenConstants;
import pl.bartlomiej.mumcommons.globalidmservice.idm.external.keycloakidm.reactor.ReactiveKeycloakService;
import reactor.core.publisher.Mono;

@Component
public class ApiKeyWebFilter implements WebFilter {

    private final DevAppHttpService devAppHttpService;
    private final ReactiveKeycloakService reactiveKeycloakService;

    public ApiKeyWebFilter(DevAppHttpService devAppHttpService, ReactiveKeycloakService reactiveKeycloakService) {
        this.devAppHttpService = devAppHttpService;
        this.reactiveKeycloakService = reactiveKeycloakService;
    }

    @NonNull
    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        return this.extractAccessTokenFromHeader(exchange)
                .flatMap(xApiKey -> reactiveKeycloakService.getAccessToken()
                        .flatMap(exchangeToken -> devAppHttpService
                                .checkToken(TokenConstants.BEARER_PREFIX + exchangeToken, xApiKey))
                        .flatMap(responseEntity -> Mono.justOrEmpty(responseEntity.getBody())
                                .flatMap(responseModel -> this.processTokenValidation(responseModel.getBody(), chain, exchange))
                                .switchIfEmpty(Mono.error(new AuthenticationServiceException("Api token validation internal error.")))
                        )
                );

    }

    private Mono<String> extractAccessTokenFromHeader(ServerWebExchange exchange) {
        String xApiKey = exchange.getRequest().getHeaders().getFirst("x-api-key");
        return Mono.justOrEmpty(xApiKey)
                .switchIfEmpty(Mono.error(new AuthenticationCredentialsNotFoundException("x-api-key header is null.")));
    }

    private Mono<Void> processTokenValidation(boolean isTokenValid, WebFilterChain chain, ServerWebExchange exchange) {
        return isTokenValid
                ? chain.filter(exchange)
                : Mono.error(new AuthenticationCredentialsNotFoundException("x-api-key header token is invalid."));
    }

}