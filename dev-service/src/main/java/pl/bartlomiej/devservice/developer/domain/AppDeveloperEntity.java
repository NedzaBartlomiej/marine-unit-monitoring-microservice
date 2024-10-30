package pl.bartlomiej.devservice.developer.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Setter
@Getter
@NoArgsConstructor
@Document("developers")
public class AppDeveloperEntity {

    private String id;
    private String email;
    private List<String> trustedIpAddresses;

    public AppDeveloperEntity(String id, String email) {
        this.id = id;
        this.email = email;
    }
}