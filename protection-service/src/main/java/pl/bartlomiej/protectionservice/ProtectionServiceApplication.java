package pl.bartlomiej.protectionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import pl.bartlomiej.protectionservice.iploginprotection.common.config.IdmServicesRepsProperties;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties(IdmServicesRepsProperties.class)
public class ProtectionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProtectionServiceApplication.class, args);
    }

}
