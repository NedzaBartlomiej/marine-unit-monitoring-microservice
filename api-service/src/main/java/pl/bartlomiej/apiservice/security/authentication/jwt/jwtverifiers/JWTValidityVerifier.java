package pl.bartlomiej.apiservice.security.authentication.jwt.jwtverifiers;

import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import pl.bartlomiej.apiservice.common.error.apiexceptions.InvalidJWTException;
import pl.bartlomiej.apiservice.security.authentication.jwt.service.JWTService;
import reactor.core.publisher.Mono;

@Component
public class JWTValidityVerifier extends AbstractJWTVerifier implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(JWTValidityVerifier.class);
    private final JWTService jwtService;
    private final ServerAuthenticationFailureHandler serverAuthenticationFailureHandler;

    public JWTValidityVerifier(JWTService jwtService,
                               @Qualifier("responseModelServerAuthenticationEntryPoint") ServerAuthenticationEntryPoint serverAuthenticationEntryPoint) {
        super(jwtService);
        this.jwtService = jwtService;
        this.serverAuthenticationFailureHandler = new ServerAuthenticationEntryPointFailureHandler(serverAuthenticationEntryPoint);
    }

    @NonNull
    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        return super.filter(exchange, chain, this.shouldNotFilter(exchange));
    }


    @Override
    protected Mono<Void> verifyToken(ServerWebExchange exchange, WebFilterChain chain, Claims claims) {
        log.info("Verifying JWT.");
        return jwtService.isValid(jwtService.extract(exchange))
                .flatMap(isValid -> {
                    if (!isValid) {
                        log.info("Invalid JWT.");
                        return Mono.error(new InvalidJWTException());
                    }
                    log.info("Valid JWT, forwarding to further flow.");
                    return chain.filter(exchange);
                })
                .onErrorResume(InvalidJWTException.class, ex ->
                        serverAuthenticationFailureHandler.onAuthenticationFailure(
                                new WebFilterExchange(exchange, chain), ex)
                );
    }
}
