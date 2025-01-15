package pl.bartlomiej.protectionservice.iploginprotection.service;

import pl.bartlomiej.protectionservice.iploginprotection.model.ProtectionServiceRequest;

public interface IpLoginProtectionService {
    Boolean executeIpLoginProtection(ProtectionServiceRequest request);

    void trustIp(String suspectLoginId, String uid);
}
