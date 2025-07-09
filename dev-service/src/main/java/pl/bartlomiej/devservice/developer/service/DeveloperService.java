package pl.bartlomiej.devservice.developer.service;

import pl.bartlomiej.devservice.developer.domain.AppDeveloperEntity;
import pl.bartlomiej.mumcommons.globalidmservice.idm.external.serviceidm.IDMServiceTemplate;

public interface DeveloperService extends IDMServiceTemplate<AppDeveloperEntity> {
    AppDeveloperEntity create(String id, String email, String ipAddress);

    void trustIp(String id, String ipAddress);

    boolean verifyIp(String id, String ipAddress);
}