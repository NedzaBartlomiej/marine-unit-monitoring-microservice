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
public class AppAdminEntity {
    private String id;
    private final LocalDateTime created = LocalDateTime.now();
    private List<String> trustedIpAddresses;

    public AppAdminEntity(String id) {
        this.id = id;
    }
}
