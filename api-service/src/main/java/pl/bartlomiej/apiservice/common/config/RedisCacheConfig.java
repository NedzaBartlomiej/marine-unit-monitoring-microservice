package pl.bartlomiej.apiservice.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import pl.bartlomiej.apiservice.ais.accesstoken.AisApiAuthTokenProvider;
import reactor.core.publisher.Mono;

import static java.time.Duration.ofMinutes;
import static org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    public static final String AIS_AUTH_TOKEN_CACHE_NAME = "AisAuthToken";
    public static final String ADDRESS_COORDS_CACHE_NAME = "AddressCoords";
    public static final String POINTS_CACHE_NAME = "Points";
    private static final Logger log = LoggerFactory.getLogger(RedisCacheConfig.class);
    private final AisApiAuthTokenProvider aisApiAuthTokenProvider;

    public RedisCacheConfig(AisApiAuthTokenProvider aisApiAuthTokenProvider) {
        this.aisApiAuthTokenProvider = aisApiAuthTokenProvider;
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder -> builder
                .withCacheConfiguration(
                        AIS_AUTH_TOKEN_CACHE_NAME,
                        defaultCacheConfig().entryTtl(ofMinutes(58)))
                .withCacheConfiguration(
                        ADDRESS_COORDS_CACHE_NAME,
                        defaultCacheConfig()
                )
                .withCacheConfiguration(
                        POINTS_CACHE_NAME,
                        defaultCacheConfig().entryTtl(ofMinutes(30))
                );
    }

    @EventListener(ApplicationReadyEvent.class)
    @CachePut(cacheNames = "AisAuthToken", key = "#result")
    public Mono<String> refreshToken() {
        log.info("Refreshing ais api auth token in cache.");
        return aisApiAuthTokenProvider.getAisAuthTokenWithoutCache().cache();
    }
}
