package pl.bartlomiej.protectionservice.iploginprotection.common.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import pl.bartlomiej.protectionservice.iploginprotection.controller.IdmServiceHttpService;

@Configuration
public class HttpInterfaceConfig {

    @Bean
    HttpServiceProxyFactory restClientProtectionFactory(@Qualifier("ipLoginProtectionAuthorizedRestClient") RestClient restClient) {
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        return HttpServiceProxyFactory.builderFor(adapter).build();
    }

    @Bean
    IdmServiceHttpService idmServiceHttpService(@Qualifier("restClientProtectionFactory") HttpServiceProxyFactory factory) {
        return factory.createClient(IdmServiceHttpService.class);
    }
}