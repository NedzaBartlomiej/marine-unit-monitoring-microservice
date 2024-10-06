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
public class Developer {

    private String id;
    private String username;
    private String email;
    private final LocalDateTime created = LocalDateTime.now();
    private List<String> trustedIpAddresses;

    public Developer(String id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }
}
