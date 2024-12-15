package pl.bartlomiej.apiservice.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.FormLoginSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.HttpBasicSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.LogoutSpec;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import pl.bartlomiej.apiservice.common.apiaccess.ApiKeyWebFilter;
import pl.bartlomiej.mumcommons.core.exceptionhandling.external.reactor.DefaultResponseModelServerAccessDeniedHandler;
import pl.bartlomiej.mumcommons.core.exceptionhandling.external.reactor.DefaultResponseModelServerAuthEntryPoint;
import pl.bartlomiej.mumcommons.globalidmservice.authconversion.external.reactor.KeycloakReactiveJwtGrantedAuthoritiesConverter;

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
                                                  DefaultResponseModelServerAuthEntryPoint authEntryPoint,
                                                  DefaultResponseModelServerAccessDeniedHandler accessDeniedHandler,
                                                  ApiKeyWebFilter apiKeyWebFilter) {
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
                .addFilterAfter(apiKeyWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    ReactiveJwtAuthenticationConverter reactiveJwtAuthenticationConverter(KeycloakReactiveJwtGrantedAuthoritiesConverter grantedAuthoritiesConverter) {
        var authenticationConverter = new ReactiveJwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return authenticationConverter;
    }
}