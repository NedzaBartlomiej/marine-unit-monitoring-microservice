package pl.bartlomiej.protectionservice.iploginprotection.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import pl.bartlomiej.loginservices.IdmServiceRepresentation;

import java.util.List;

@ConfigurationProperties(prefix = "login-services-reps")
public record IdmServicesRepsProperties(List<IdmServiceRepresentation> idmServiceRepresentations) {

}