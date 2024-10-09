package pl.bartlomiej.adminservice.config;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.bartlomiej.mummicroservicecommons.exceptionhandling.external.servlet.ErrorResponseModelErrorController;

@Configuration
public class ErrorControllerConfig {

    @Bean
    ErrorController errorController() {
        return new ErrorResponseModelErrorController();
    }
}
