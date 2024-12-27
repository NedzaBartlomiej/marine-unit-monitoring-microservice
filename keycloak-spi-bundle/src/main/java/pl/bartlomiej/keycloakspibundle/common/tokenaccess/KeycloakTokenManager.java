package pl.bartlomiej.keycloakspibundle.common.tokenaccess;

import org.keycloak.models.KeycloakSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class KeycloakTokenManager {
    private static final Logger log = LoggerFactory.getLogger(KeycloakTokenManager.class);
    private final KeycloakTokenFetcher keycloakTokenFetcher;
    private String token;
    private final Lock tokenLock = new ReentrantLock();

    public KeycloakTokenManager(KeycloakTokenFetcher keycloakTokenFetcher) {
        this.keycloakTokenFetcher = keycloakTokenFetcher;
    }

    public String getToken(final KeycloakSession keycloakSession) {
        String executorThreadName = Thread.currentThread().getName();
        if (this.token != null) {
            log.debug("Token isn't null, returning available token. Thread name: {}", executorThreadName);
            return this.token;
        }

        log.debug("Token is null, trying to lock token resource. Thread name: {}", executorThreadName);
        this.tokenLock.lock();
        try {
            log.debug("Locked token resource. Executing resource operations. Thread name: {}", executorThreadName);
            if (this.token == null) {
                log.debug("Token still null after lock. Thread name: {}", executorThreadName);
                this.refreshToken(keycloakSession);
            } else {
                log.debug("Token isn't null after lock, returning available token. Thread name: {}", executorThreadName);
            }
        } finally {
            log.debug("Unlocking token resource. Thread name: {}", executorThreadName);
            this.tokenLock.unlock();
        }
        return this.token;
    }

    // todo - concurrent optimization - refresh token once, and not by every thread
    public void refreshToken(final KeycloakSession keycloakSession) {
        this.token = this.keycloakTokenFetcher.fetchToken(keycloakSession);
    }
}