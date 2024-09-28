package pl.bartlomiej.adminservice.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Admin {
    private String id;
    private String login;
    private String email;
    private final LocalDateTime created = LocalDateTime.now();

    public Admin(String id, String login, String email) {
        this.id = id;
        this.login = login;
        this.email = email;
    }
}
