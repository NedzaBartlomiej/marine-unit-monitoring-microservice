package pl.bartlomiej.protectionservice.iploginprotection.suspectlogin;

public interface SuspectLoginService {

    SuspectLogin create(String ipAddress, String uid, String loginServiceHostname);
}
