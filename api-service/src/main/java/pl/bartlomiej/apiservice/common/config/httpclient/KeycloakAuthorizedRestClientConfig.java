package pl.bartlomiej.apiservice.common.config.httpclient;

import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;
import pl.bartlomiej.mumcommons.core.webtools.requesthandler.authorizedhandler.*;

@Configuration
public class KeycloakAuthorizedRestClientConfig {
    @Bean
    RestClient devServiceAuthorizedRestClient(@Qualifier("devServiceAuthorizedRequestInterceptor") ClientHttpRequestInterceptor interceptor) {
        return RestClient.builder()
                .baseUrl("http://dev-service:8083/")
                .requestInterceptor(interceptor)
                .build();
    }

    @Bean
    ClientHttpRequestInterceptor devServiceAuthorizedRequestInterceptor(@Qualifier("devServiceAuthorizedInterceptorTokenManager") AuthorizedInterceptorTokenManager tokenManager) {
        return new AuthorizedRequestInterceptor(tokenManager);
    }

    @Bean
    AuthorizedInterceptorTokenManager devServiceAuthorizedInterceptorTokenManager(@Qualifier("devServiceAuthorizedInterceptorTokenProvider") AuthorizedInterceptorTokenProvider tokenProvider) {
        return new MemoryAuthorizedInterceptorTokenManager(tokenProvider);
    }

    @Bean
    AuthorizedInterceptorTokenProvider devServiceAuthorizedInterceptorTokenProvider(Keycloak keycloak) {
        return new KeycloakAuthorizedInterceptorTokenProvider(keycloak.tokenManager());
    }
}