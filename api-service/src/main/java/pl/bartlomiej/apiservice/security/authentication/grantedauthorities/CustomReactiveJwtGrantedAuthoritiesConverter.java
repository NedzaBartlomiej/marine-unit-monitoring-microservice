package pl.bartlomiej.apiservice.security.authentication.grantedauthorities;


import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import pl.bartlomiej.apiservice.user.User;
import pl.bartlomiej.apiservice.user.service.UserService;
import reactor.core.publisher.Flux;

@Component
public class CustomReactiveJwtGrantedAuthoritiesConverter implements Converter<Jwt, Flux<GrantedAuthority>> {

    private final UserService userService;

    public CustomReactiveJwtGrantedAuthoritiesConverter(UserService userService) {
        this.userService = userService;
    }


    @Override
    public Flux<GrantedAuthority> convert(Jwt source) {
        return userService.processAuthenticationFlowUser(
                        source.getSubject(),
                        source.getClaimAsString("name"),
                        source.getClaimAsString("email"),
                        source.getClaimAsString("iss")
                )
                .flatMapIterable(User::getRoles)
                .map(role -> new UserRoleAuthority(role.name()));
    }
}