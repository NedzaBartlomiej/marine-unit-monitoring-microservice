package pl.bartlomiej.apiservice.common.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import pl.bartlomiej.apiservice.common.apiaccess.DevServiceHttpService;

@Configuration
public class HttpInterfaceConfig {

    @Bean
    HttpServiceProxyFactory restClientDevFactory(@Qualifier("devServiceAuthorizedRestClient") RestClient restClient) {
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        return HttpServiceProxyFactory.builderFor(adapter).build();
    }

    @Bean
    DevServiceHttpService devHttpService(@Qualifier("restClientDevFactory") HttpServiceProxyFactory factory) {
        return factory.createClient(DevServiceHttpService.class);
    }
}