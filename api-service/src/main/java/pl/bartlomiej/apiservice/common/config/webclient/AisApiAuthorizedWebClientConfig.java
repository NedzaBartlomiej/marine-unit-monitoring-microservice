package pl.bartlomiej.apiservice.common.config.webclient;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import pl.bartlomiej.apiservice.ais.AisApiAuthorizedExchangeTokenProvider;
import pl.bartlomiej.mumcommons.core.webtools.requesthandler.authorizedhandler.reactor.AuthorizedExchangeFilterFunction;
import pl.bartlomiej.mumcommons.core.webtools.requesthandler.authorizedhandler.reactor.AuthorizedExchangeTokenManager;
import pl.bartlomiej.mumcommons.core.webtools.requesthandler.authorizedhandler.reactor.MemoryAuthorizedExchangeTokenManager;

@Configuration
public class AisApiAuthorizedWebClientConfig {
    @Bean
    WebClient aisApiAuthorizedWebClient(@Qualifier("aisApiAuthorizedExchangeFilterFunction") ExchangeFilterFunction filterFunction) {
        return WebClient.builder()
                .filter(filterFunction)
                .build();
    }

    @Bean
    ExchangeFilterFunction aisApiAuthorizedExchangeFilterFunction(@Qualifier("aisApiAuthorizedExchangeTokenManager") AuthorizedExchangeTokenManager tokenManager) {
        return new AuthorizedExchangeFilterFunction(tokenManager);
    }

    @Bean
    AuthorizedExchangeTokenManager aisApiAuthorizedExchangeTokenManager(AisApiAuthorizedExchangeTokenProvider tokenProvider) {
        return new MemoryAuthorizedExchangeTokenManager(tokenProvider);
    }
}