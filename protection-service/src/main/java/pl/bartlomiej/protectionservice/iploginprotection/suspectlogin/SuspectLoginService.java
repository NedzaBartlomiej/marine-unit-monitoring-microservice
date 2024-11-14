package pl.bartlomiej.protectionservice.iploginprotection.suspectlogin;

import pl.bartlomiej.mummicroservicecommons.config.loginservicereps.LoginServiceRepresentation;

public interface SuspectLoginService {

    SuspectLogin create(String ipAddress, String uid, LoginServiceRepresentation loginServiceRepresentation);

    void delete(String id);

    SuspectLogin get(String id, String uid);
}
