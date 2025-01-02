import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.TokenManager;
import org.keycloak.representations.AccessToken;
import pl.bartlomiej.keycloakspibundle.common.tokenaccess.KeycloakTokenFetcher;
import pl.bartlomiej.keycloakspibundle.common.tokenaccess.KeycloakTokenStorage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

// AI GENERATED TEST - NOT OFFICIAL RELEASE
class KeycloakTokenStorageTest {

    private KeycloakTokenStorage tokenManager;
    private KeycloakTokenFetcher mockFetcher;
    private KeycloakSession mockSession;
    private TokenManager mockTokenManager;

    @BeforeEach
    void setup() {
        mockFetcher = mock(KeycloakTokenFetcher.class);
        mockSession = mock(KeycloakSession.class);
        mockTokenManager = mock(TokenManager.class);
        when(mockSession.tokens()).thenReturn(mockTokenManager);
        tokenManager = new KeycloakTokenStorage(mockFetcher);
    }

    @Test
    void testGetValidTokenThreadSafety() throws InterruptedException {
        // Mock token fetching
        when(mockFetcher.fetchToken(mockSession)).thenReturn("new-token");

        // Simulate token expiry check
        when(mockTokenManager.decode(anyString(), eq(AccessToken.class)))
                .thenAnswer(invocation -> {
                    String token = invocation.getArgument(0);
                    AccessToken mockToken = mock(AccessToken.class);
                    // Mark token as expired if it is not "new-token"
                    when(mockToken.isExpired()).thenReturn(!"new-token".equals(token));
                    return mockToken;
                });

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        // Execute multiple threads to call getValidToken
        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                String token = tokenManager.getValidToken(mockSession);
                assertEquals("new-token", token); // Ensure all threads get the same token
            });
        }

        executor.shutdown();
        boolean finished = executor.awaitTermination(5, TimeUnit.SECONDS);

        // Verify thread-safety
        verify(mockFetcher, times(1)).fetchToken(mockSession); // Ensure token fetched only once
        assertEquals(true, finished, "All threads should finish within the timeout");
    }
}