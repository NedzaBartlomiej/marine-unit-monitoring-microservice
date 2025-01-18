package pl.bartlomiej.protectionservice.iploginprotection.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.bartlomiej.loginservices.IdmServiceRepResolver;

@Configuration
public class IdmServiceResolverConfig {
    @Bean
    IdmServiceRepResolver idmServiceResolver(IdmServicesRepsProperties idmServicesRepsProperties) {
        return new IdmServiceRepResolver(idmServicesRepsProperties.idmServiceRepresentations());
    }
}
