package pl.bartlomiej.apiservice.user;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiej.apiservice.common.helper.ResponseModel;
import pl.bartlomiej.apiservice.common.util.ControllerResponseUtil;
import pl.bartlomiej.apiservice.security.tokenverification.emailverification.service.EmailVerificationService;
import pl.bartlomiej.apiservice.user.dto.UserDtoMapper;
import pl.bartlomiej.apiservice.user.dto.UserReadDto;
import pl.bartlomiej.apiservice.user.dto.UserSaveDto;
import pl.bartlomiej.apiservice.user.service.UserService;
import reactor.core.publisher.Mono;

import java.security.Principal;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static pl.bartlomiej.apiservice.common.util.ControllerResponseUtil.buildResponse;
import static pl.bartlomiej.apiservice.common.util.ControllerResponseUtil.buildResponseModel;
import static reactor.core.publisher.Mono.just;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;
    private final UserDtoMapper userDtoMapper;
    private final EmailVerificationService emailVerificationService;
    private final TransactionalOperator transactionalOperator;

    public UserController(UserService userService,
                          UserDtoMapper userDtoMapper,
                          EmailVerificationService emailVerificationService,
                          TransactionalOperator transactionalOperator) {
        this.userService = userService;
        this.userDtoMapper = userDtoMapper;
        this.emailVerificationService = emailVerificationService;
        this.transactionalOperator = transactionalOperator;
    }


    @PreAuthorize("hasRole(T(pl.bartlomiej.apiservice.user.nested.Role).SIGNED.name())")
    @GetMapping("/me")
    public Mono<ResponseEntity<ResponseModel<UserReadDto>>> getAuthenticatedUser(Principal principal) {
        return userService.identifyUser(principal.getName())
                .flatMap(id -> userService.getUser(id)
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
                        )
                );
    }

    @PostMapping
    public Mono<ResponseEntity<ResponseModel<UserReadDto>>> createUser(
            @RequestBody @Valid UserSaveDto userSaveDto,
            @RequestHeader(name = "X-Forwarded-For") String xForwardedFor) {
        return this.processTransactionalUserCreation(userSaveDto, xForwardedFor)
                .map(user ->
                        buildResponse(
                                CREATED,
                                buildResponseModel(
                                        "CREATED,EMAIL_SENT",
                                        CREATED,
                                        userDtoMapper.mapToReadDto(user),
                                        "user"
                                )
                        )
                );
    }

    private Mono<User> processTransactionalUserCreation(UserSaveDto userSaveDto, String ipAddress) {
        return transactionalOperator.transactional(
                userService.createUser(userDtoMapper.mapFrom(userSaveDto), ipAddress)
                        .flatMap(user ->
                                emailVerificationService.issue(user.getId(), null)
                                        .then(just(user))
                        )
        );
    }

    @PreAuthorize("hasRole(T(pl.bartlomiej.apiservice.user.nested.Role).ADMIN.name())")
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

    @PreAuthorize("hasRole(T(pl.bartlomiej.apiservice.user.nested.Role).SIGNED.name())")
    @PatchMapping("/me/two-factor-auth-enabled/{enabled}")
    public Mono<ResponseEntity<ResponseModel<Void>>> toggleIsTwoFactorAuthEnabled(@PathVariable Boolean enabled, Principal principal) {
        return userService.identifyUser(principal.getName())
                .flatMap(id -> userService.updateIsTwoFactorAuthEnabled(id, enabled))
                .then(just(
                        buildResponse(
                                OK,
                                buildResponseModel(
                                        "UPDATED",
                                        OK,
                                        null,
                                        null
                                )
                        )
                ));
    }
}