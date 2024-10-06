package pl.bartlomiej.apiservice.user.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiej.apiservice.common.helper.ResponseModel;
import pl.bartlomiej.apiservice.common.util.ControllerResponseUtil;
import pl.bartlomiej.apiservice.user.dto.UserDtoMapper;
import pl.bartlomiej.apiservice.user.dto.UserReadDto;
import pl.bartlomiej.apiservice.user.dto.UserRegisterDto;
import pl.bartlomiej.apiservice.user.service.UserService;
import reactor.core.publisher.Mono;

import java.security.Principal;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;
    private final UserDtoMapper userDtoMapper;

    public UserController(UserService userService,
                          UserDtoMapper userDtoMapper) {
        this.userService = userService;
        this.userDtoMapper = userDtoMapper;
    }


    @PreAuthorize("hasRole(T(pl.bartlomiej.apiservice.user.domain.UserKeycloakRole).API_USER.getRole())")
    @GetMapping("/me")
    public Mono<ResponseEntity<ResponseModel<UserReadDto>>> getAuthenticatedUser(Principal principal) {
        return userService.getUser(principal.getName())
                .map(user ->
                        ControllerResponseUtil.buildResponse(
                                OK,
                                ControllerResponseUtil.buildResponseModel(
                                        null,
                                        OK,
                                        userDtoMapper.mapToReadDto(user),
                                        "user"
                                )
                        )
                );
    }

    @PostMapping
    public Mono<ResponseEntity<ResponseModel<UserReadDto>>> createUser(@RequestBody @Valid UserRegisterDto userRegisterDto) {
        return userService.create(userRegisterDto, "127.0.0.1")
                .map(user -> ControllerResponseUtil.buildResponse(
                        CREATED,
                        ControllerResponseUtil.buildResponseModel(
                                "CREATED",
                                CREATED,
                                userDtoMapper.mapToReadDto(user),
                                "user"
                        )
                ));
    }
}