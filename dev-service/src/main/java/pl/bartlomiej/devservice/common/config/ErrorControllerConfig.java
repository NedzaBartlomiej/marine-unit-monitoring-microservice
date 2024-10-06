package pl.bartlomiej.devservice.common.config;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.bartlomiej.springexceptionhandlingbundle.external.servlet.ErrorResponseModelErrorController;

@Configuration
public class ErrorControllerConfig {

    @Bean
    ErrorController errorController() {
        return new ErrorResponseModelErrorController();
    }
}
