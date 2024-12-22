package pl.bartlomiej.apiservice.common.config;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.time.Duration.ofMinutes;
import static org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    public static final String AIS_AUTH_TOKEN_CACHE_NAME = "AisAuthToken";
    public static final String ADDRESS_COORDS_CACHE_NAME = "AddressCoords";
    public static final String POINTS_CACHE_NAME = "Points";

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
}
