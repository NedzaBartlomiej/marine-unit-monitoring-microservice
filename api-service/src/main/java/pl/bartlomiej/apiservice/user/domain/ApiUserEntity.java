package pl.bartlomiej.apiservice.user.domain;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.bartlomiej.apiservice.user.nested.trackedship.TrackedShip;

import java.util.Collections;
import java.util.Set;

@Getter
@Document(collection = "users")
public class ApiUserEntity {

    private final String id;
    private final Set<TrackedShip> trackedShips;
    private final Set<String> trustedIpAddresses;

    public ApiUserEntity(String id, Set<TrackedShip> trackedShips, Set<String> trustedIpAddresses) {
        this.id = id;
        this.trackedShips = trackedShips == null
                ? Collections.emptySet()
                : Collections.unmodifiableSet(trackedShips);
        this.trustedIpAddresses = trustedIpAddresses == null
                ? Collections.emptySet()
                : Collections.unmodifiableSet(trustedIpAddresses);
    }
}