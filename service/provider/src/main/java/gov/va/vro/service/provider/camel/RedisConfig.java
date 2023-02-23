package gov.va.vro.service.provider.camel;

import org.springframework.context.annotation.Bean;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.api.reactive.RedisPubSubReactiveCommands;
import io.lettuce.core.resource.ClientResources;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private Integer redisPort;


    @Bean("redis-client")
    public RedisClient redisClient() {
        return RedisClient.create(
            ClientResources.builder().build(), "redis://" + redisHost + ":" + redisPort
        );
    }

    @Bean("redis-sub")
    public RedisPubSubReactiveCommands<String, String> redisPubSubAsyncCommands(RedisClient redisClient) {
        return redisClient.connectPubSub().reactive();
    }
}