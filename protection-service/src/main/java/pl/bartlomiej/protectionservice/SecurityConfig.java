package pl.bartlomiej.protectionservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import pl.bartlomiej.mummicroservicecommons.authconversion.external.servlet.KeycloakJwtGrantedAuthoritiesConverter;
import pl.bartlomiej.mummicroservicecommons.exceptionhandling.external.servlet.DefaultResponseModelAccessDeniedHandler;
import pl.bartlomiej.mummicroservicecommons.exceptionhandling.external.servlet.DefaultResponseModelAuthEntryPoint;

@Configuration
@EnableMethodSecurity
/// DEMO CLASS
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            JwtAuthenticationConverter authConverter,
                                            DefaultResponseModelAuthEntryPoint authEntryPoint,
                                            DefaultResponseModelAccessDeniedHandler accessDeniedHandler) throws Exception {
        return http
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .anyRequest().permitAll()
                )
                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
                        .jwt(jwtConfigurer ->
                                jwtConfigurer.jwtAuthenticationConverter(authConverter)
                        )
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .exceptionHandling(c -> c
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter(KeycloakJwtGrantedAuthoritiesConverter authoritiesConverter) {
        var authConverter = new JwtAuthenticationConverter();
        authConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return authConverter;
    }
}