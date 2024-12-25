package pl.bartlomiej.apiservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.bartlomiej.mumcommons.core.webtools.requesthandler.authorizedhandler.reactor.MemoryAuthorizedExchangeTokenManager;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MemoryAuthorizedExchangeTokenManagerTest {

    private MemoryAuthorizedExchangeTokenManager tokenManager;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @BeforeEach
    public void setup() {
        // Simulacja provider, który pobiera token
        pl.bartlomiej.mumcommons.core.webtools.requesthandler.authorizedhandler.reactor.AuthorizedExchangeTokenProvider tokenProvider = () -> {
            // Zakładamy, że token jest pobierany asynchronicznie
            return Mono.just("token123" + Math.random());
        };

        tokenManager = new MemoryAuthorizedExchangeTokenManager(tokenProvider);
    }

    @Test
    public void testTokenManagerConcurrency() throws InterruptedException, ExecutionException {
        int numberOfThreads = 10;  // Liczba wątków próbujących pobrać token jednocześnie

        // Lista do przechowywania tokenów uzyskanych przez poszczególne wątki
        List<String> tokens = new CopyOnWriteArrayList<>();

        // Używamy CountDownLatch, aby poczekać na zakończenie wszystkich wątków
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        CyclicBarrier barrier = new CyclicBarrier(numberOfThreads);

        // Uruchamiamy wątki
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    barrier.await(); // Czekaj na wszystkie wątki, aby rozpoczęły równocześnie
                    tokenManager.getToken()
                            .doOnTerminate(latch::countDown) // Zmniejszamy licznik po zakończeniu
                            .doOnError(throwable -> System.err.println("Error: " + throwable))
                            .doOnSuccess(tokens::add) // Dodaj token do listy
                            .subscribe();
                } catch (InterruptedException | BrokenBarrierException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        latch.await(); // Czekamy na zakończenie wszystkich wątków

        // Sprawdzamy, że token nie jest nullem
        assertNotNull(tokens, "Token list should not be null");

        // Wypisujemy tokeny
        System.out.println("Tokens retrieved by threads: ");
        tokens.forEach(System.out::println);

        executorService.shutdown();
    }

    // Mockowana implementacja tokenProvider
    private interface AuthorizedExchangeTokenProvider {
        Mono<String> fetchToken();
    }
}
