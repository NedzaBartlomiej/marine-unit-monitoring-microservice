package pl.bartlomiej.apiservice.common.config;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.server.WebExceptionHandler;
import pl.bartlomiej.mummicroservicecommons.exceptionhandling.external.reactor.ErrorResponseModelWebExceptionHandler;

@Configuration
public class WebExceptionHandlerConfig {

    @Bean
    WebExceptionHandler webExceptionHandler(ErrorAttributes errorAttributes,
                                            WebProperties webProperties,
                                            ApplicationContext applicationContext,
                                            ServerCodecConfigurer serverCodecConfigurer) {
        return new ErrorResponseModelWebExceptionHandler(errorAttributes, webProperties, applicationContext, serverCodecConfigurer);
    }
}
