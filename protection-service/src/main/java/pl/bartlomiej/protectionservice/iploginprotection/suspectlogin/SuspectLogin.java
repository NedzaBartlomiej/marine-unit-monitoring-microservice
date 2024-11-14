package pl.bartlomiej.protectionservice.iploginprotection.suspectlogin;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.bartlomiej.mummicroservicecommons.config.loginservicereps.LoginServiceRepresentation;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@Document("suspectLogins")
public class SuspectLogin {

    private String id;
    private String uid;
    private String ipAddress;
    private LoginServiceRepresentation loginServiceRepresentation;
    private String hostname;
    private String city;
    private String region;
    private String country;
    private String loc;
    private final LocalDateTime time = LocalDateTime.now();

    public SuspectLogin(String uid, String ipAddress, LoginServiceRepresentation loginServiceRepresentation, String hostname, String city, String region, String country, String loc) {
        this.uid = uid;
        this.ipAddress = ipAddress;
        this.loginServiceRepresentation = loginServiceRepresentation;
        this.hostname = hostname;
        this.city = city;
        this.region = region;
        this.country = country;
        this.loc = loc;
    }
}