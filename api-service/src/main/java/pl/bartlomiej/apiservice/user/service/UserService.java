package pl.bartlomiej.apiservice.user.service;

import pl.bartlomiej.apiservice.user.User;
import pl.bartlomiej.apiservice.user.dto.UserSaveDto;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<User> getUser(String id);

    Mono<User> createUser(UserSaveDto userSaveDto, String ipAddress);

    Mono<Void> deleteUser(String id);

    Mono<Boolean> isUserExists(String id);

    Mono<Void> trustIpAddress(String id, String ipAddress);
}