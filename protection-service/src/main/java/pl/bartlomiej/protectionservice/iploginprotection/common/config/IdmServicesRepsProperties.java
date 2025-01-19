package pl.bartlomiej.protectionservice.iploginprotection.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import pl.bartlomiej.idmservicesreps.IdmServiceRepresentation;

import java.util.List;

@ConfigurationProperties
public record IdmServicesRepsProperties(List<IdmServiceRepresentation> idmServiceRepresentations) {

}