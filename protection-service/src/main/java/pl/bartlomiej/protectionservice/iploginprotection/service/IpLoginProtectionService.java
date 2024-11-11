package pl.bartlomiej.protectionservice.iploginprotection.service;

import pl.bartlomiej.protectionservice.iploginprotection.model.IpLoginProtectionRequest;

public interface IpLoginProtectionService {
    String executeIpLoginProtection(IpLoginProtectionRequest request);
}
