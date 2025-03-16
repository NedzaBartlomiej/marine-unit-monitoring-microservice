package pl.bartlomiej.apiservice.point.activepoint;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


// todo consider sense of this - why I don't do the same instead with the Point and creating this?
//  -> ActivePoint usage causes a lot of calls to db (exists check) by adding to keep connectivity with Points,
//  and this is so bad!
@Document(collection = "activePoints")
public class ActivePoint {

    @Id
    private String mmsi;
    private String name;

    public ActivePoint() {
    }

    public ActivePoint(String mmsi, String name) {
        this.mmsi = mmsi;
        this.name = name;
    }

    public String getMmsi() {
        return mmsi;
    }

    public String getName() {
        return name;
    }
}