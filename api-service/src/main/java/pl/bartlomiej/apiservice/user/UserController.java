package pl.bartlomiej.apiservice.user;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiej.apiservice.common.helper.ResponseModel;
import pl.bartlomiej.apiservice.common.util.ControllerResponseUtil;
import pl.bartlomiej.apiservice.user.dto.UserDtoMapper;
import pl.bartlomiej.apiservice.user.dto.UserReadDto;
import pl.bartlomiej.apiservice.user.dto.UserSaveDto;
import pl.bartlomiej.apiservice.user.service.UserService;
import reactor.core.publisher.Mono;

import java.security.Principal;

import static org.springframework.http.HttpStatus.OK;
import static pl.bartlomiej.apiservice.common.util.ControllerResponseUtil.buildResponse;
import static pl.bartlomiej.apiservice.common.util.ControllerResponseUtil.buildResponseModel;
import static reactor.core.publisher.Mono.just;

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


    @PreAuthorize("hasRole(T(pl.bartlomiej.apiservice.user.UserKeycloakRole).API_USER.name())")
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
    public Mono<ResponseEntity<ResponseModel<UserReadDto>>> createUser(@RequestBody @Valid UserSaveDto userSaveDto) {
        // todo
        return Mono.empty();
    }

    // todo ADMIN ROLE FROM THE ADMINSERVICE
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ResponseModel<Void>>> deleteUser(@PathVariable String id) {
        return userService.deleteUser(id)
                .then(just(
                        buildResponse(
                                OK,
                                buildResponseModel(
                                        "DELETED",
                                        OK,
                                        null,
                                        null
                                )
                        )
                ));
    }
}