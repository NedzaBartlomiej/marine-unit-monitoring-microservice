package pl.bartlomiej.apiservice.common.config.webclient;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;
import pl.bartlomiej.apiservice.aisapi.AisApiAuthorizedInterceptorTokenProvider;
import pl.bartlomiej.mumcommons.core.webtools.requesthandler.authorizedhandler.AuthorizedInterceptorTokenManager;
import pl.bartlomiej.mumcommons.core.webtools.requesthandler.authorizedhandler.AuthorizedRequestInterceptor;
import pl.bartlomiej.mumcommons.core.webtools.requesthandler.authorizedhandler.MemoryAuthorizedInterceptorTokenManager;

@Configuration
public class AisApiAuthorizedRestClientConfig {
    @Bean
    RestClient aisApiAuthorizedRestClient(@Qualifier("aisApiAuthorizedRequestInterceptor") ClientHttpRequestInterceptor interceptor) {
        return RestClient.builder()
                .requestInterceptor(interceptor)
                .build();
    }

    @Bean
    ClientHttpRequestInterceptor aisApiAuthorizedRequestInterceptor(@Qualifier("aisApiAuthorizedInterceptorTokenManager") AuthorizedInterceptorTokenManager tokenManager) {
        return new AuthorizedRequestInterceptor(tokenManager);
    }

    @Bean
    AuthorizedInterceptorTokenManager aisApiAuthorizedInterceptorTokenManager(AisApiAuthorizedInterceptorTokenProvider tokenProvider) {
        return new MemoryAuthorizedInterceptorTokenManager(tokenProvider);
    }
}