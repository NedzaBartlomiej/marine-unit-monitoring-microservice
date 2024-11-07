package pl.bartlomiej.protectionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ProtectionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProtectionServiceApplication.class, args);
    }

}
