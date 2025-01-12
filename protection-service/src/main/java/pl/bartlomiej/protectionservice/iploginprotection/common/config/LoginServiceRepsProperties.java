package pl.bartlomiej.protectionservice.iploginprotection.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import pl.bartlomiej.loginservices.LoginServiceRepresentation;

import java.util.List;

@ConfigurationProperties(prefix = "login-service-reps")
public record LoginServiceRepsProperties(List<LoginServiceRepresentation> loginServiceRepresentations) {

}