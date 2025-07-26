package pl.bartlomiej.devservice.developer.domain;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;
import java.util.Set;

@Getter
@Document("developers")
public class AppDeveloperEntity {

    private final String id;
    private final String email;
    private final Set<String> trustedIpAddresses;

    public AppDeveloperEntity(String id, String email, Set<String> trustedIpAddresses) {
        this.id = id;
        this.email = email;
        this.trustedIpAddresses = trustedIpAddresses == null
                ? Collections.emptySet()
                : Collections.unmodifiableSet(trustedIpAddresses);
    }
}