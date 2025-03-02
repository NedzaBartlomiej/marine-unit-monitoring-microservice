package pl.bartlomiej.apiservice.common.config;

import com.mongodb.ConnectionString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
public class MongoConfig {

    private final String mongoConnectionString;

    public MongoConfig(@Value("${spring.data.mongodb.uri}") String mongoConnectionString
    ) {
        this.mongoConnectionString = mongoConnectionString;
    }

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory() {
        return new SimpleMongoClientDatabaseFactory(new ConnectionString(this.mongoConnectionString));
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory factory) {
        return new MongoTemplate(factory);
    }
}