package pl.bartlomiej.apiservice.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import pl.bartlomiej.apiservice.common.apiaccess.ApiKeyWebFilter;
import pl.bartlomiej.mumcommons.core.exceptionhandling.external.DefaultResponseModelAccessDeniedHandler;
import pl.bartlomiej.mumcommons.core.exceptionhandling.external.DefaultResponseModelAuthEntryPoint;
import pl.bartlomiej.mumcommons.globalidmservice.authconversion.external.KeycloakJwtGrantedAuthoritiesConverter;

import java.util.List;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final List<String> postOpenEndpoints = List.of(
            "*/users"
    );
    private final List<String> getOpenEndpoints = List.of(
            "*/ship-points"
    );

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            JwtAuthenticationConverter authenticationConverter,
                                            DefaultResponseModelAuthEntryPoint authEntryPoint,
                                            DefaultResponseModelAccessDeniedHandler accessDeniedHandler,
                                            ApiKeyWebFilter apiKeyWebFilter) throws Exception {
        return http
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    postOpenEndpoints.forEach(e -> auth.requestMatchers(POST, e).permitAll());
                    getOpenEndpoints.forEach(e -> auth.requestMatchers(GET, e).permitAll());
                    auth.anyRequest().authenticated();
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
                .addFilterBefore(apiKeyWebFilter, BasicAuthenticationFilter.class)
                .build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter(KeycloakJwtGrantedAuthoritiesConverter authoritiesConverter) {
        var authConverter = new JwtAuthenticationConverter();
        authConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return authConverter;
    }
}