import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.models.KeycloakSession;
import pl.bartlomiej.keycloakspibundle.common.tokenaccess.KeycloakTokenFetcher;
import pl.bartlomiej.keycloakspibundle.common.tokenaccess.KeycloakTokenManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

// AI GENERATED TEST - NOT OFFICIAL RELEASE
class KeycloakTokenManagerTest {
    private KeycloakTokenFetcher keycloakTokenFetcher;
    private KeycloakTokenManager tokenManager;
    private KeycloakSession keycloakSession;

    @BeforeEach
    void setUp() {
        keycloakTokenFetcher = mock(KeycloakTokenFetcher.class);
        keycloakSession = mock(KeycloakSession.class);
        tokenManager = new KeycloakTokenManager(keycloakTokenFetcher);
    }

    @Test
    void shouldFetchTokenWhenTokenIsNull() {
        // Arrange
        String expectedToken = "newToken";
        when(keycloakTokenFetcher.fetchToken(keycloakSession)).thenReturn(expectedToken);

        // Act
        String token = tokenManager.getToken(keycloakSession);

        // Assert
        assertEquals(expectedToken, token);
        verify(keycloakTokenFetcher, times(1)).fetchToken(keycloakSession);
    }

    @Test
    void shouldReturnExistingTokenIfNotNull() {
        // Arrange
        String expectedToken = "existingToken";
        when(keycloakTokenFetcher.fetchToken(keycloakSession)).thenReturn(expectedToken);
        tokenManager.getToken(keycloakSession); // Fetch token initially

        // Act
        String token = tokenManager.getToken(keycloakSession);

        // Assert
        assertEquals(expectedToken, token);
        verify(keycloakTokenFetcher, times(1)).fetchToken(keycloakSession); // Should not fetch again
    }

    @Test
    void shouldRefreshTokenWhenRequested() {
        // Arrange
        String initialToken = "initialToken";
        String refreshedToken = "refreshedToken";
        when(keycloakTokenFetcher.fetchToken(keycloakSession)).thenReturn(initialToken, refreshedToken);

        // Act
        tokenManager.getToken(keycloakSession); // Fetch initial token
        tokenManager.refreshToken(keycloakSession); // Refresh token

        // Assert
        String token = tokenManager.getToken(keycloakSession);
        assertEquals(refreshedToken, token);
        verify(keycloakTokenFetcher, times(2)).fetchToken(keycloakSession); // Fetch initial and refreshed token
    }

    @Test
    void shouldHandleConcurrentAccessSafely() throws InterruptedException {
        // Arrange
        String expectedToken = "concurrentToken" + Math.random();
        when(keycloakTokenFetcher.fetchToken(keycloakSession)).thenReturn(expectedToken);

        int threadCount = 10;
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {

            // Act
            for (int i = 0; i < threadCount; i++) {
                executorService.submit(() -> {
                    String token = tokenManager.getToken(keycloakSession);
                    // Log the token each thread retrieves
                    System.out.println(Thread.currentThread().getName() + " retrieved token: " + token);
                });
            }
            executorService.shutdown();
            assertTrue(executorService.awaitTermination(5, TimeUnit.SECONDS));

            // Assert
            String token = tokenManager.getToken(keycloakSession);
            assertEquals(expectedToken, token);
            verify(keycloakTokenFetcher, times(1)).fetchToken(keycloakSession); // Token should be fetched only once
        }
    }
}