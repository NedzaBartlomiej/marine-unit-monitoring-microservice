package pl.bartlomiej.apiservice.user.service;

import pl.bartlomiej.apiservice.user.domain.User;
import pl.bartlomiej.mummicroservicecommons.globalidmservice.external.serviceidm.reactor.ReactiveIDMServiceTemplate;
import reactor.core.publisher.Mono;

public interface UserService extends ReactiveIDMServiceTemplate<User> {

    Mono<Boolean> isUserExists(String id);

    Mono<Void> trustIp(String id, String ipAddress);

    Mono<Boolean> verifyIp(String id, String ipAddress);
}