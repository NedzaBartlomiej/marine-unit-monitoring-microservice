package pl.bartlomiej.protectionservice.iploginprotection.suspectlogin;

public interface SuspectLoginService {

    SuspectLogin create(String ipAddress, String uid, String loginServiceClientId);

    void delete(String id);

    SuspectLogin get(String id, String uid);
}
