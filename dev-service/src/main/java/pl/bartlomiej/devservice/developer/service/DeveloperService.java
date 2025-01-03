package pl.bartlomiej.devservice.developer.service;

import pl.bartlomiej.devservice.developer.domain.AppDeveloperEntity;
import pl.bartlomiej.mumcommons.globalidmservice.idm.external.serviceidm.servlet.IDMServiceTemplate;

public interface DeveloperService extends IDMServiceTemplate<AppDeveloperEntity> {
    void trustIp(String id, String ipAddress);

    boolean verifyIp(String id, String ipAddress);
}