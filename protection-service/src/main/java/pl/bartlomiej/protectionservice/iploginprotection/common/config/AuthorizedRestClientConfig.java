package pl.bartlomiej.protectionservice.iploginprotection.common.config;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;
import pl.bartlomiej.mumcommons.core.webtools.requestinterceptor.authorizedinterceptor.*;

@Slf4j
@Configuration
public class AuthorizedRestClientConfig {

    @Bean
    RestClient ipLoginProtectionRestClient(@Qualifier("protectionAuthorizedRequestInterceptor") ClientHttpRequestInterceptor interceptor) {
        return RestClient.builder()
                .requestInterceptor(interceptor)
                .build();
    }

    @Bean
    ClientHttpRequestInterceptor protectionAuthorizedRequestInterceptor(@Qualifier("protectionAuthorizedClientTokenManager") AuthorizedInterceptorTokenManager tokenManager) {
        return new AuthorizedRequestInterceptor(tokenManager);
    }

    @Bean
    AuthorizedInterceptorTokenManager protectionAuthorizedClientTokenManager(@Qualifier("protectionAuthorizedClientTokenProvider") AuthorizedInterceptorTokenProvider tokenProvider) {
        return new MemoryAuthorizedInterceptorTokenManager(tokenProvider);
    }

    @Bean
    AuthorizedInterceptorTokenProvider protectionAuthorizedClientTokenProvider(Keycloak keycloak) {
        return new KeycloakAuthorizedInterceptorTokenProvider(keycloak.tokenManager());
    }
}