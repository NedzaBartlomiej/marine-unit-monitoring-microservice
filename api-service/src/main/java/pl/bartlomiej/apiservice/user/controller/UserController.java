package pl.bartlomiej.apiservice.user.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiej.apiservice.user.domain.User;
import pl.bartlomiej.apiservice.user.domain.dto.UserRegisterDto;
import pl.bartlomiej.apiservice.user.service.UserService;
import pl.bartlomiej.mummicroservicecommons.model.response.ResponseModel;
import reactor.core.publisher.Mono;

import java.security.Principal;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PreAuthorize("hasRole(T(pl.bartlomiej.apiservice.user.domain.UserKeycloakRole).API_USER.getRole())")
    @GetMapping("/me")
    public Mono<ResponseEntity<ResponseModel<User>>> getAuthenticatedUser(Principal principal) {
        return userService.getUser(principal.getName())
                .map(user -> ResponseEntity.ok(
                        new ResponseModel.Builder<User>(OK)
                                .body(user)
                                .build())
                );
    }

    @PostMapping
    public Mono<ResponseEntity<ResponseModel<User>>> createUser(@RequestBody @Valid UserRegisterDto userRegisterDto) {
        return userService.create(userRegisterDto, "127.0.0.1")
                .map(user -> ResponseEntity.status(CREATED)
                        .body(new ResponseModel.Builder<User>(CREATED)
                                .body(user)
                                .build())
                );
    }
}