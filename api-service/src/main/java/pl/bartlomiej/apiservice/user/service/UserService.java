package pl.bartlomiej.apiservice.user.service;

import pl.bartlomiej.apiservice.user.domain.ApiUserEntity;
import pl.bartlomiej.mumcommons.keycloakintegration.idm.external.serviceidm.IDMServiceTemplate;

public interface UserService extends IDMServiceTemplate<ApiUserEntity> {

    ApiUserEntity create(String id, String ipAddress);

    void trustIp(String id, String ipAddress);

    boolean verifyIp(String id, String ipAddress);
}