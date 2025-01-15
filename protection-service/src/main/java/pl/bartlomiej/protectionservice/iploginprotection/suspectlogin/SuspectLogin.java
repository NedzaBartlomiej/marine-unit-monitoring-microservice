package pl.bartlomiej.protectionservice.iploginprotection.suspectlogin;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@Document("suspectLogins")
public class SuspectLogin {
    // todo - cleaning function
    //  (firstly - delete all connected/with the same reason/invocation suspectLogins
    //  when the user trust that suspectLogin's IP)

    private String id;
    private String uid;
    private String ipAddress;
    private String idmServiceClientId;
    private String hostname;
    private String city;
    private String region;
    private String country;
    private String loc;
    private LocalDateTime time = LocalDateTime.now();

    public SuspectLogin(String uid, String ipAddress, String idmServiceClientId, String hostname, String city, String region, String country, String loc) {
        this.uid = uid;
        this.ipAddress = ipAddress;
        this.idmServiceClientId = idmServiceClientId;
        this.hostname = hostname;
        this.city = city;
        this.region = region;
        this.country = country;
        this.loc = loc;
    }
}