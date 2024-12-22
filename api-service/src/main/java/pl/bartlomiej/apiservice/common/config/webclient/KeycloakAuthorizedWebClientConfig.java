package pl.bartlomiej.apiservice.common.config.webclient;

import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import pl.bartlomiej.mumcommons.core.webtools.requesthandler.authorizedhandler.reactor.*;

@Configuration
public class KeycloakAuthorizedWebClientConfig {
    @Bean
    WebClient devServiceAuthorizedWebClient(@Qualifier("devServiceAuthorizedExchangeFilterFunction") ExchangeFilterFunction filterFunction) {
        return WebClient.builder()
                .baseUrl("http://dev-service:8083/")
                .filter(filterFunction)
                .build();
    }

    @Bean
    ExchangeFilterFunction devServiceAuthorizedExchangeFilterFunction(@Qualifier("devServiceAuthorizedExchangeTokenManager") AuthorizedExchangeTokenManager tokenManager) {
        return new AuthorizedExchangeFilterFunction(tokenManager);
    }

    @Bean
    AuthorizedExchangeTokenManager devServiceAuthorizedExchangeTokenManager(@Qualifier("devServiceAuthorizedExchangeTokenProvider") AuthorizedExchangeTokenProvider tokenProvider) {
        return new MemoryAuthorizedExchangeTokenManager(tokenProvider);
    }

    @Bean
    AuthorizedExchangeTokenProvider devServiceAuthorizedExchangeTokenProvider(Keycloak keycloak) {
        return new KeycloakAuthorizedExchangeTokenProvider(keycloak.tokenManager());
    }
}