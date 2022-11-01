package gov.va.vro.consolegroovy

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate

@Configuration
@EnableJpaRepositories("gov.va.vro.persistence.repository")
@EntityScan("gov.va.vro.persistence.model")
class VroConsoleConfig {

  @Autowired
  RedisProperties redisProperties;

  @Bean
  LettuceConnectionFactory lettuceConnectionFactory() {
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisProperties.host, redisProperties.port)
    config.database = redisProperties.database
    config.username = redisProperties.username
    config.password = redisProperties.password
    return new LettuceConnectionFactory(config)
  }

  @Bean
  RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(lettuceConnectionFactory());
    // template.setValueSerializer(new GenericToStringSerializer<Object>(Object))
    return template;
  }
}
