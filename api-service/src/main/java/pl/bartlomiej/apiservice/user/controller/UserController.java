package pl.bartlomiej.apiservice.user.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiej.apiservice.user.domain.ApiUserEntity;
import pl.bartlomiej.apiservice.user.domain.dto.UserRegisterDto;
import pl.bartlomiej.apiservice.user.service.UserService;
import pl.bartlomiej.loginservices.IdmServiceRepUserCreationDto;
import pl.bartlomiej.mumcommons.core.model.response.ResponseModel;
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
    public Mono<ResponseEntity<ResponseModel<ApiUserEntity>>> getAuthenticatedUser(Principal principal) {
        return userService.getEntity(principal.getName())
                .map(user -> ResponseEntity.ok(
                        new ResponseModel.Builder<ApiUserEntity>(OK, true)
                                .body(user)
                                .build())
                );
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<ResponseModel<ApiUserEntity>>> register(@RequestBody @Valid UserRegisterDto userRegisterDto) {
        return userService.register(userRegisterDto, "127.0.0.1")
                .map(user -> ResponseEntity.status(CREATED)
                        .body(new ResponseModel.Builder<ApiUserEntity>(CREATED, true)
                                .body(user)
                                .build())
                );
    }

    @PreAuthorize("hasRole('USER_CREATION_AUTHENTICATOR')")
    @PostMapping
    public Mono<ResponseEntity<ResponseModel<ApiUserEntity>>> create(@RequestBody IdmServiceRepUserCreationDto idmServiceRepUserCreationDto) {
        return userService.create(
                        idmServiceRepUserCreationDto.uid(),
                        idmServiceRepUserCreationDto.ipAddress()
                )
                .map(apiUserEntity -> ResponseEntity.status(CREATED)
                        .body(new ResponseModel.Builder<ApiUserEntity>(CREATED, true)
                                .body(apiUserEntity)
                                .build())
                );
    }

    @PreAuthorize("hasRole('IP_LOGIN_PROTECTOR')")
    @GetMapping("/{id}/trustedIpAddresses")
    public Mono<ResponseEntity<ResponseModel<Boolean>>> verifyIp(@PathVariable String id,
                                                                 @RequestParam String ipAddress) {
        return userService.verifyIp(id, ipAddress)
                .map(isTrusted -> ResponseEntity.status(OK)
                        .body(new ResponseModel.Builder<Boolean>(OK, true)
                                .body(isTrusted)
                                .build())
                );
    }

    @PreAuthorize("hasRole('IP_LOGIN_PROTECTOR')")
    @PostMapping("/{id}/trustedIpAddresses")
    public Mono<ResponseEntity<ResponseModel<Void>>> trustIp(@PathVariable String id,
                                                             @RequestParam String ipAddress) {
        return userService.trustIp(id, ipAddress)
                .map(isTrusted -> ResponseEntity.status(OK)
                        .body(new ResponseModel.Builder<Void>(OK, true)
                                .build())
                );
    }
}