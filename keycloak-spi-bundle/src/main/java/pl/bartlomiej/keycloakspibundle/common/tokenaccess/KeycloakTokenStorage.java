package pl.bartlomiej.keycloakspibundle.common.tokenaccess;

import org.keycloak.models.KeycloakSession;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class KeycloakTokenStorage {
    private static final Logger log = LoggerFactory.getLogger(KeycloakTokenStorage.class);
    private final KeycloakTokenFetcher keycloakTokenFetcher;
    private String token;
    private final Lock tokenLock = new ReentrantLock();

    public KeycloakTokenStorage(KeycloakTokenFetcher keycloakTokenFetcher) {
        this.keycloakTokenFetcher = keycloakTokenFetcher;
    }

    public String getValidToken(final KeycloakSession keycloakSession) {
        if (MDC.get("traceId") == null) {
            MDC.put("traceId", UUID.randomUUID().toString());
        }
        log.debug("Obtaining valid access token");
        if (isTokenValid(keycloakSession)) {
            log.trace("Token is valid, returning available token.");
            return this.token;
        }
        log.trace("Token is invalid, attempting to lock and refresh.");
        this.tokenLock.lock();
        try {
            log.trace("Locked token resource. Executing resource operations.");
            if (!isTokenValid(keycloakSession)) {
                this.refreshToken(keycloakSession);
            }
            log.trace("Token has been refreshed by another thread, returning this valid token.");
        } finally {
            log.trace("Unlocking token resource.");
            this.tokenLock.unlock();
            MDC.clear();
        }
        return this.token;
    }

    private boolean isTokenValid(final KeycloakSession keycloakSession) {
        if (this.token == null) {
            return false;
        }
        AccessToken decodedToken = keycloakSession.tokens().decode(this.token, AccessToken.class);
        return !decodedToken.isExpired();
    }

    private void refreshToken(final KeycloakSession keycloakSession) {
        log.trace("Refreshing token.");
        this.token = this.keycloakTokenFetcher.fetchToken(keycloakSession);
    }
}