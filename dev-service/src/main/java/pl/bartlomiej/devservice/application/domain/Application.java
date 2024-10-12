package pl.bartlomiej.devservice.application.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Document("applications")
public class Application {

    private String id;
    private String name;
    private String appToken;
    private String requestDesc;
    private String devId;
    private ApplicationRequestStatus requestStatus = ApplicationRequestStatus.PENDING;
    private Boolean isBlocked = false;
    private LocalDateTime created = LocalDateTime.now();

    public Application(String devId, String requestDesc, String name) {
        this.devId = devId;
        this.requestDesc = requestDesc;
        this.name = name;
    }
}