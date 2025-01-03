package pl.bartlomiej.apiservice.common.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import pl.bartlomiej.apiservice.common.apiaccess.DevServiceHttpService;

@Configuration
public class HttpInterfaceConfig {

    @Bean
    HttpServiceProxyFactory webClientDevFactory(@Qualifier("devServiceAuthorizedWebClient") WebClient webClient) {
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        return HttpServiceProxyFactory.builderFor(adapter).build();
    }

    @Bean
    DevServiceHttpService devAppHttpExchangeService(@Qualifier("webClientDevFactory") HttpServiceProxyFactory factory) {
        return factory.createClient(DevServiceHttpService.class);
    }
}