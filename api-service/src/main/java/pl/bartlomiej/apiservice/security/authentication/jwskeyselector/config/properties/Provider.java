package pl.bartlomiej.apiservice.security.authentication.jwskeyselector.config.properties;

import java.net.URL;

public record Provider(String issuerUri, URL jwksUri) {
}
