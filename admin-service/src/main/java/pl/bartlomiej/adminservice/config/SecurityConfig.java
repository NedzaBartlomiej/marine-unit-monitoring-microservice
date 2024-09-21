package pl.bartlomiej.adminservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import pl.bartlomiej.jwtgrantedauthorityconverter.external.servlet.KeycloakJwtGrantedAuthoritiesConverter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationConverter authConverter) throws Exception {
        return http
                .sessionManagement(sessionConfigurer -> sessionConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests.anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
                        .jwt(jwtConfigurer ->
                                jwtConfigurer.jwtAuthenticationConverter(authConverter)
                        )
                )
                .build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter(KeycloakJwtGrantedAuthoritiesConverter authoritiesConverter) {
        var authConverter = new JwtAuthenticationConverter();
        authConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return authConverter;
    }

    @Bean
    KeycloakJwtGrantedAuthoritiesConverter keycloakJwtGrantedAuthoritiesConverter() {
        return new KeycloakJwtGrantedAuthoritiesConverter();
    }
}
