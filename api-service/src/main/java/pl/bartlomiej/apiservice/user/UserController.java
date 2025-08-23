package pl.bartlomiej.apiservice.user;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiej.apiservice.user.domain.ApiUserEntity;
import pl.bartlomiej.apiservice.user.domain.dto.UserRegisterDto;
import pl.bartlomiej.apiservice.user.service.UserService;
import pl.bartlomiej.idmservicesreps.IdmServiceRepUserCreationDto;
import pl.bartlomiej.mumcommons.core.model.response.ResponseModel;

import java.security.Principal;

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
    public ResponseEntity<ResponseModel<ApiUserEntity>> getAuthenticatedUser(Principal principal) {
        return ResponseEntity.status(OK)
                .body(new ResponseModel.Builder<ApiUserEntity>(OK, true)
                        .body(userService.getEntity(principal.getName()))
                        .build()
                );
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseModel<ApiUserEntity>> register(@RequestBody @Valid UserRegisterDto userRegisterDto,
                                                                 @RequestHeader(name = "X-Real-IP") String xRealIp) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseModel.Builder<ApiUserEntity>(HttpStatus.CREATED, true)
                        .body(userService.register(userRegisterDto, xRealIp))
                        .build()
                );
    }

    @PreAuthorize("hasRole('USER_CREATION_AUTHENTICATOR')")
    @PostMapping
    public ResponseEntity<ResponseModel<ApiUserEntity>> create(@RequestBody IdmServiceRepUserCreationDto idmServiceRepUserCreationDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseModel.Builder<ApiUserEntity>(HttpStatus.CREATED, true)
                        .body(userService.create(
                                        idmServiceRepUserCreationDto.uid(),
                                        idmServiceRepUserCreationDto.ipAddress()
                                )
                        )
                        .build()
                );
    }

    @PreAuthorize("hasRole('IP_LOGIN_PROTECTOR')")
    @GetMapping("/{id}/trustedIpAddresses")
    public ResponseEntity<ResponseModel<Boolean>> verifyIp(@PathVariable String id,
                                                           @RequestParam String ipAddress) {
        return ResponseEntity.ok(
                new ResponseModel.Builder<Boolean>(HttpStatus.OK, true)
                        .body(userService.verifyIp(id, ipAddress))
                        .build()
        );
    }

    @PreAuthorize("hasRole('IP_LOGIN_PROTECTOR')")
    @PostMapping("/{id}/trustedIpAddresses")
    public ResponseEntity<ResponseModel<Void>> trustIp(@PathVariable String id,
                                                       @RequestParam String ipAddress) {
        userService.trustIp(id, ipAddress);
        return ResponseEntity.ok(
                new ResponseModel.Builder<Void>(HttpStatus.OK, true)
                        .build()
        );
    }
}