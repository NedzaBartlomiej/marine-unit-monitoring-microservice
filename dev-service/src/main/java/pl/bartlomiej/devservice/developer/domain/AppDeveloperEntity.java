package pl.bartlomiej.devservice.developer.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document("developers")
public class AppDeveloperEntity {

    private String id;
    private final LocalDateTime created = LocalDateTime.now();
    private List<String> trustedIpAddresses;

    public AppDeveloperEntity(String id) {
        this.id = id;
    }
}
