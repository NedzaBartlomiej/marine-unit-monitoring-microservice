package pl.bartlomiej.apiservice.common.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import pl.bartlomiej.apiservice.shippoint.ShipPoint;

@Configuration
public class RedisTemplateConfig {

    @Bean
    public RedisTemplate<String, ShipPoint> shipPointRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, ShipPoint> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());
        template.setHashValueSerializer(RedisSerializer.json());
        return template;
    }
}
