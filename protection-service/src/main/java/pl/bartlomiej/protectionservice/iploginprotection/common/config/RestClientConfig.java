package pl.bartlomiej.protectionservice.iploginprotection.common.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import pl.bartlomiej.mumcommons.core.webtools.retry.unauthorized.KeycloakRetryClientTokenProvider;
import pl.bartlomiej.mumcommons.core.webtools.retry.unauthorized.RetryClientTokenProvider;
import pl.bartlomiej.mumcommons.core.webtools.retry.unauthorized.UnauthorizedRetryRequestInterceptor;
import pl.bartlomiej.mumcommons.globalidmservice.idm.external.keycloakidm.servlet.KeycloakService;

@Configuration
public class RestClientConfig {

    @Bean
    RestClient ipLoginProtectionRestClient(@Qualifier("protectionRetryClientTokenProvider") RetryClientTokenProvider tokenProvider) {
        return RestClient.builder()
                .requestInterceptor(new UnauthorizedRetryRequestInterceptor(tokenProvider))
                .build();
    }

    @Bean
    RetryClientTokenProvider protectionRetryClientTokenProvider(KeycloakService keycloakService) {
        return new KeycloakRetryClientTokenProvider(keycloakService);
    }
}