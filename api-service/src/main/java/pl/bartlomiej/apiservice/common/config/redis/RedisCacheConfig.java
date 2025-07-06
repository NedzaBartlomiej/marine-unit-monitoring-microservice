package pl.bartlomiej.apiservice.common.config.redis;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    public static final String ADDRESS_COORDS_CACHE_NAME = "AddressCoords";

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder -> builder
                .withCacheConfiguration(
                        ADDRESS_COORDS_CACHE_NAME,
                        defaultCacheConfig()
                );
    }
}
