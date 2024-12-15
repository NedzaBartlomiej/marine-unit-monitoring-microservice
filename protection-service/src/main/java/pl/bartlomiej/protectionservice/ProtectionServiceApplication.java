package pl.bartlomiej.protectionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import pl.bartlomiej.mumcommons.core.config.loginservicereps.LoginServiceRepsProperties;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties(LoginServiceRepsProperties.class)
public class ProtectionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProtectionServiceApplication.class, args);
    }

}
