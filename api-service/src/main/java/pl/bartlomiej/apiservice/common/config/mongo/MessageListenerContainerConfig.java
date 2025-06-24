package pl.bartlomiej.apiservice.common.config.mongo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.messaging.DefaultMessageListenerContainer;
import org.springframework.data.mongodb.core.messaging.MessageListenerContainer;

@Configuration
public class MessageListenerContainerConfig {
    @Bean
    MessageListenerContainer messageListenerContainer(MongoTemplate mongoTemplate) {
        return new DefaultMessageListenerContainer(mongoTemplate);
    }
}
