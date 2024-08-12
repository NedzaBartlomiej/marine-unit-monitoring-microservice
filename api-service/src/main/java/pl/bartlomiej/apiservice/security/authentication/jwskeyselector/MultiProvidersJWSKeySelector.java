package pl.bartlomiej.apiservice.security.authentication.jwskeyselector;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.JWTClaimsSetAwareJWSKeySelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.bartlomiej.apiservice.common.error.authexceptions.JWKsUrlNotFoundException;
import pl.bartlomiej.apiservice.security.authentication.jwskeyselector.config.JWSKeySelectorConfig;
import pl.bartlomiej.apiservice.security.authentication.jwskeyselector.config.properties.MultiProvidersJWSKeySelectorProperties;
import pl.bartlomiej.apiservice.security.authentication.jwskeyselector.config.properties.Provider;
import pl.bartlomiej.apiservice.security.authentication.jwt.service.JWTService;

import java.net.URL;
import java.security.Key;
import java.util.List;

@Component
public class MultiProvidersJWSKeySelector implements JWTClaimsSetAwareJWSKeySelector<SecurityContext> {

    private static final Logger log = LoggerFactory.getLogger(MultiProvidersJWSKeySelector.class);
    public final String tokenIssuer;
    private final JWSKeySelectorConfig jwsKeySelectorConfig;
    private final MultiProvidersJWSKeySelectorProperties keySelectorProperties;
    private final JWTService jwtService;

    public MultiProvidersJWSKeySelector(JWSKeySelectorConfig jwsKeySelectorConfig,
                                        MultiProvidersJWSKeySelectorProperties keySelectorProperties,
                                        JWTService jwtService,
                                        @Value("${project-properties.security.jwt.issuer}") String tokenIssuer) {
        this.jwsKeySelectorConfig = jwsKeySelectorConfig;
        this.keySelectorProperties = keySelectorProperties;
        this.jwtService = jwtService;
        this.tokenIssuer = tokenIssuer;
    }

    @Override
    public List<? extends Key> selectKeys(JWSHeader jwsHeader, JWTClaimsSet jwtClaimsSet, SecurityContext securityContext) throws KeySourceException {
        if (jwtClaimsSet.getIssuer().equals(this.tokenIssuer)) {
            log.info("Returning secret key for registration authentication based token.");
            return List.of(jwtService.getSigningKey());
        }

        log.info("Attempting to return key set for OAuth2 issuer.");
        var selector = jwsKeySelectorConfig.getJWSKeySelector(this.getJwksUrl(jwtClaimsSet.getIssuer()));
        log.info("Returning key set.");
        return selector.selectJWSKeys(jwsHeader, securityContext);
    }

    private URL getJwksUrl(String issuer) {
        log.info("Recognising token provider and returning dependent JWKs url.");
        return keySelectorProperties.providers().stream()
                .filter(provider -> provider.issuerUri().equals(issuer))
                .map(Provider::jwksUri)
                .findFirst()
                .orElseThrow(JWKsUrlNotFoundException::new);
    }
}