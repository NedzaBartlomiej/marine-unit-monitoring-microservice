package pl.bartlomiej.apiservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import pl.bartlomiej.apiservice.security.authentication.jwskeyselector.config.properties.MultiProvidersJWSKeySelectorProperties;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(MultiProvidersJWSKeySelectorProperties.class)
@EnableDiscoveryClient
public class ApiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiServiceApplication.class, args);
    }

}
