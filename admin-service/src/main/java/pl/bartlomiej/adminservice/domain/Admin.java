package pl.bartlomiej.adminservice.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document("admins")
public class Admin {
    private String id;
    private String login;
    private String email;
    private final LocalDateTime created = LocalDateTime.now();
    private List<String> trustedIpAddresses;

    public Admin(String id, String login, String email) {
        this.id = id;
        this.login = login;
        this.email = email;
    }
}
