package pl.bartlomiej.protectionservice.iploginprotection.common.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import pl.bartlomiej.mummicroservicecommons.globalidmservice.external.keycloakidm.servlet.KeycloakService;
import pl.bartlomiej.mummicroservicecommons.webtools.retryclient.unauthorized.external.RetryClientTokenProvider;

@Configuration
public class RestClientConfig {

    @Bean
    RestClient ipLoginProtectionRestClient(@Qualifier("unauthorizedRetryRestClient") RestClient restClient) {
        return restClient;
    }

    @Bean
    RetryClientTokenProvider retryClientTokenProvider(KeycloakService keycloakService) {
        return new KeycloakRetryClientTokenProvider(keycloakService);
    }
}