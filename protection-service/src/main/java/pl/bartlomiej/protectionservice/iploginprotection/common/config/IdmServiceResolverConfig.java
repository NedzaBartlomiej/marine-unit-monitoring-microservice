package pl.bartlomiej.protectionservice.iploginprotection.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.bartlomiej.loginservices.IdmServiceResolver;

@Configuration
public class IdmServiceResolverConfig {
    @Bean
    IdmServiceResolver idmServiceResolver(IdmServicesRepsProperties idmServicesRepsProperties) {
        return new IdmServiceResolver(idmServicesRepsProperties.idmServiceRepresentations());
    }
}
