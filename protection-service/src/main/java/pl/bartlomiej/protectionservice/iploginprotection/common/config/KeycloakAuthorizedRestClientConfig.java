package pl.bartlomiej.protectionservice.iploginprotection.common.config;

import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;
import pl.bartlomiej.mumcommons.core.webtools.requesthandler.authorizedhandler.servlet.*;

@Configuration
public class KeycloakAuthorizedRestClientConfig {

    @Bean
    RestClient ipLoginProtectionAuthorizedRestClient(@Qualifier("protectionAuthorizedRequestInterceptor") ClientHttpRequestInterceptor interceptor) {
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