package gov.va.vro.service.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
class RedisConnectionConfig {

  // Loads properties spring.redis.*
  final RedisProperties redisProperties;

  @Bean
  LettuceConnectionFactory lettuceConnectionFactory() {
    RedisStandaloneConfiguration config =
        new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
    config.setDatabase(redisProperties.getDatabase());
    config.setUsername(redisProperties.getUsername());
    config.setPassword(redisProperties.getPassword());
    return new LettuceConnectionFactory(config);
  }

  // https://stackoverflow.com/questions/37402717/camel-redis-automatically-prepends-string-to-key
  // https://dzone.com/articles/using-redis-spring
  @Bean
  RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(lettuceConnectionFactory());
    template.setDefaultSerializer(new StringRedisSerializer());
    return template;
  }
}
