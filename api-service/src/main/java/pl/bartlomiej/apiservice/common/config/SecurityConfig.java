package pl.bartlomiej.apiservice.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.FormLoginSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.HttpBasicSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.LogoutSpec;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import pl.bartlomiej.apiservice.common.error.ErrorResponseModelServerAccessDeniedHandler;
import pl.bartlomiej.apiservice.common.error.ErrorResponseModelServerAuthEntryPoint;
import pl.bartlomiej.jwtgrantedauthorityconverter.external.reactor.KeycloakReactiveJwtGrantedAuthoritiesConverter;
import pl.bartlomiej.springexceptionhandlingbundle.external.GlobalHttpStatusResolver;
import pl.bartlomiej.springexceptionhandlingbundle.external.reactor.ErrorResponseModelServerExceptionHandler;

import java.util.List;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    private final List<String> postOpenEndpoints = List.of(
            "*/users"
    );
    private final List<String> getOpenEndpoints = List.of(
            "*/points"
    );

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                  ReactiveJwtAuthenticationConverter authenticationConverter,
                                                  ErrorResponseModelServerAuthEntryPoint authEntryPoint,
                                                  ErrorResponseModelServerAccessDeniedHandler accessDeniedHandler) {
        return http
                .httpBasic(HttpBasicSpec::disable)
                .formLogin(FormLoginSpec::disable)
                .logout(LogoutSpec::disable)
                .csrf(CsrfSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(auth -> {
                    postOpenEndpoints.forEach(e -> auth.pathMatchers(POST, e).permitAll());
                    getOpenEndpoints.forEach(e -> auth.pathMatchers(GET, e).permitAll());
                    auth.anyExchange().authenticated();
                })
                .oauth2ResourceServer(oAuth2ResourceServerSpec ->
                        oAuth2ResourceServerSpec
                                .jwt(jwtSpec -> jwtSpec.jwtAuthenticationConverter(authenticationConverter))
                                .authenticationEntryPoint(authEntryPoint)
                                .accessDeniedHandler(accessDeniedHandler)
                )
                .exceptionHandling(exceptionHandlingSpec ->
                        exceptionHandlingSpec
                                .authenticationEntryPoint(authEntryPoint)
                                .accessDeniedHandler(accessDeniedHandler)
                )
                .build();
    }

    @Bean
    ReactiveJwtAuthenticationConverter reactiveJwtAuthenticationConverter(KeycloakReactiveJwtGrantedAuthoritiesConverter grantedAuthoritiesConverter) {
        var authenticationConverter = new ReactiveJwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return authenticationConverter;
    }

    @Bean
    ErrorResponseModelServerExceptionHandler errorResponseModelServerExceptionHandler(ObjectMapper objectMapper,
                                                                                      GlobalHttpStatusResolver globalHttpStatusResolver) {
        return new ErrorResponseModelServerExceptionHandler(objectMapper, globalHttpStatusResolver);
    }
}