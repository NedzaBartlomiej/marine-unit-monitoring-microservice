package pl.bartlomiej.apiservice.user.service;

import pl.bartlomiej.apiservice.user.domain.User;
import pl.bartlomiej.mummicroservicecommons.globalidmservice.external.serviceidm.reactor.ReactiveIDMServiceTemplate;
import reactor.core.publisher.Mono;

public interface UserService extends ReactiveIDMServiceTemplate<User> {

    Mono<User> getUser(String id);

    Mono<Boolean> isUserExists(String id);

    Mono<Void> trustIpAddress(String id, String ipAddress);
}