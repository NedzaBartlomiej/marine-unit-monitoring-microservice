package pl.bartlomiej.apiservice.user.service;

import pl.bartlomiej.apiservice.user.domain.ApiUserEntity;
import pl.bartlomiej.mumcommons.globalidmservice.idm.external.serviceidm.reactor.ReactiveIDMServiceTemplate;
import reactor.core.publisher.Mono;

public interface UserService extends ReactiveIDMServiceTemplate<ApiUserEntity> {

    Mono<ApiUserEntity> create(String id, String ipAddress);

    Mono<Void> handleUserDoesNotExists(String id);

    Mono<Void> trustIp(String id, String ipAddress);

    Mono<Boolean> verifyIp(String id, String ipAddress);
}