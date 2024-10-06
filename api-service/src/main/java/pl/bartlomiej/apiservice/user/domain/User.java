package pl.bartlomiej.apiservice.user.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.bartlomiej.apiservice.user.nested.trackedship.TrackedShip;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Document(collection = "users")
public class User {

    private String id;
    private String username;
    private String email;
    private List<TrackedShip> trackedShips;
    private List<String> trustedIpAddresses;

    public User(String id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }
}