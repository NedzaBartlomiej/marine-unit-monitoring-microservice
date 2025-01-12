package pl.bartlomiej.protectionservice.iploginprotection.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.bartlomiej.loginservices.LoginServiceResolver;

@Configuration
public class LoginServiceResolverConfig {
    @Bean
    LoginServiceResolver loginServiceResolver(LoginServiceRepsProperties loginServiceRepsProperties) {
        return new LoginServiceResolver(loginServiceRepsProperties.loginServiceRepresentations());
    }
}
