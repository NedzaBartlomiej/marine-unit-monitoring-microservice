package pl.bartlomiej.apiservice.security.authentication.service;

import pl.bartlomiej.apiservice.security.authentication.AuthResponse;
import pl.bartlomiej.apiservice.user.User;
import reactor.core.publisher.Mono;

public interface AuthenticationService {
    Mono<AuthResponse> authenticate(User user, String authPassword, String ipAddress);
}
