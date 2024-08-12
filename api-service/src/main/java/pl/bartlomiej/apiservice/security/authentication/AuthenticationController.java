package pl.bartlomiej.apiservice.security.authentication;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import pl.bartlomiej.apiservice.common.helper.ResponseModel;
import pl.bartlomiej.apiservice.common.util.ControllerResponseUtil;
import pl.bartlomiej.apiservice.security.authentication.jwt.refreshtokenendpoint.RefreshTokenConsumer;
import pl.bartlomiej.apiservice.security.authentication.jwt.service.JWTService;
import pl.bartlomiej.apiservice.security.authentication.service.AuthenticationService;
import pl.bartlomiej.apiservice.security.tokenverification.twofactorauth.service.TwoFactorAuthService;
import pl.bartlomiej.apiservice.user.dto.UserAuthDto;
import pl.bartlomiej.apiservice.user.service.UserService;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.springframework.http.HttpStatus.OK;
import static pl.bartlomiej.apiservice.common.util.ControllerResponseUtil.buildResponse;
import static pl.bartlomiej.apiservice.common.util.ControllerResponseUtil.buildResponseModel;
import static reactor.core.publisher.Mono.just;

@RestController
@RequestMapping("/v1/authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final JWTService jwtService;
    private final TwoFactorAuthService twoFactorAuthService;

    public AuthenticationController(AuthenticationService authenticationService,
                                    UserService userService,
                                    JWTService jwtService, TwoFactorAuthService twoFactorAuthService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.jwtService = jwtService;
        this.twoFactorAuthService = twoFactorAuthService;
    }

    @GetMapping("/authenticate")
    public Mono<ResponseEntity<ResponseModel<Map<String, String>>>> authenticate(
            @RequestBody @Valid UserAuthDto userAuthDto,
            @RequestHeader(name = "X-Forwarded-For") String xForwardedFor) {
        return userService.getUserByEmail(userAuthDto.getEmail())
                .flatMap(user -> authenticationService.authenticate(
                                        user, userAuthDto.getPassword(), xForwardedFor
                                )
                                .map(authResponse ->
                                        ControllerResponseUtil.buildResponse(
                                                OK,
                                                ControllerResponseUtil.buildResponseModel(
                                                        authResponse.message(),
                                                        OK,
                                                        authResponse.tokens(),
                                                        "authenticationTokens"
                                                )
                                        )
                                )
                );
    }

    @GetMapping("/authenticate/{code}")
    public Mono<ResponseEntity<ResponseModel<Map<String, String>>>> authenticate(@PathVariable String code) {
        return twoFactorAuthService.verify(code)
                .flatMap(twoFactorAuthService::performVerifiedTokenAction)
                .map(tokens ->
                        buildResponse(
                                OK,
                                buildResponseModel(
                                        "AUTHENTICATED",
                                        OK,
                                        tokens,
                                        "authenticationTokens"
                                )
                        )
                );
    }

    @RefreshTokenConsumer
    @PreAuthorize("hasRole(T(pl.bartlomiej.apiservice.user.nested.Role).SIGNED.name())")
    @GetMapping("/refresh-access-token")
    public Mono<ResponseEntity<ResponseModel<Map<String, String>>>> refreshAccessToken(ServerWebExchange exchange) {
        String token = jwtService.extract(exchange);
        return jwtService.refreshAccessToken(
                token,
                jwtService.extractSubject(token),
                jwtService.extractEmail(token)
        ).map(tokens ->
                buildResponse(
                        OK,
                        buildResponseModel(
                                "REFRESHED,REFRESH_TOKEN_ROTATED",
                                OK,
                                tokens,
                                "authenticationTokens"
                        )
                )
        );
    }

    @RefreshTokenConsumer
    @PreAuthorize("hasRole(T(pl.bartlomiej.apiservice.user.nested.Role).SIGNED.name())")
    @GetMapping("/invalidate-authentication")
    public Mono<ResponseEntity<ResponseModel<Void>>> invalidateAuthentication(ServerWebExchange exchange) {
        return jwtService.invalidateAuthentication(
                jwtService.extract(exchange)
        ).then(just(
                buildResponse(
                        OK,
                        buildResponseModel(
                                "INVALIDATED",
                                OK,
                                null,
                                null
                        )
                )
        ));
    }
}
