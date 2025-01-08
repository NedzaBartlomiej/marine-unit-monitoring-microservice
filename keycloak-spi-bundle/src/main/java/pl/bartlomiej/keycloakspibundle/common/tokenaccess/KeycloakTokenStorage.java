package pl.bartlomiej.keycloakspibundle.common.tokenaccess;

import org.keycloak.models.KeycloakSession;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        String executorThreadName = Thread.currentThread().getName();
        if (isTokenValid(keycloakSession)) {
            log.debug("Token is valid, returning available token. Thread name: {}", executorThreadName);
            return this.token;
        }
        log.debug("Token is invalid, attempting to lock and refresh. Thread name: {}", executorThreadName);
        this.tokenLock.lock();
        try {
            log.debug("Locked token resource. Executing resource operations. Thread name: {}", executorThreadName);
            if (!isTokenValid(keycloakSession)) {
                this.refreshToken(keycloakSession);
            }
            log.debug("Token has been refreshed by another thread, returning this valid token. Thread name: {}", executorThreadName);
        } finally {
            log.debug("Unlocking token resource. Thread name: {}", executorThreadName);
            this.tokenLock.unlock();
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
        log.debug("Refreshing token. Thread: {}", Thread.currentThread().getName());
        this.token = this.keycloakTokenFetcher.fetchToken(keycloakSession);
    }
}