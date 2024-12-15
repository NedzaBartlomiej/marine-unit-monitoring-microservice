package pl.bartlomiej.protectionservice.iploginprotection.common.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import pl.bartlomiej.protectionservice.iploginprotection.controller.LoginServiceHttpService;

@Configuration
public class HttpInterfaceConfig {

    @Bean
    HttpServiceProxyFactory ipLoginProtectionFactory(@Qualifier("ipLoginProtectionRestClient") RestClient restClient) {
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        return HttpServiceProxyFactory.builderFor(adapter).build();
    }

    @Bean
    LoginServiceHttpService loginServiceHttpService(@Qualifier("ipLoginProtectionFactory") HttpServiceProxyFactory factory) {
        return factory.createClient(LoginServiceHttpService.class);
    }
}