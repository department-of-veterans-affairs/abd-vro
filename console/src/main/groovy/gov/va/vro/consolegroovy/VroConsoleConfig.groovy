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
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@EnableJpaRepositories("gov.va.vro.persistence.repository")
@EntityScan("gov.va.vro.persistence.model")
class VroConsoleConfig {

  @Autowired
  RedisProperties redisProperties

  @Bean
  LettuceConnectionFactory lettuceConnectionFactory() {
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisProperties.host, redisProperties.port)
    config.database = redisProperties.database
    config.username = redisProperties.username
    config.password = redisProperties.password
    new LettuceConnectionFactory(config)
  }

  // https://stackoverflow.com/questions/37402717/camel-redis-automatically-prepends-string-to-key
  // https://dzone.com/articles/using-redis-spring
  @Bean("redisTemplate")
  RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> template = new RedisTemplate<>()
    template.setConnectionFactory(lettuceConnectionFactory())
    template.setDefaultSerializer(new StringRedisSerializer())
    // template.setKeySerializer(new StringRedisSerializer());
    // template.setHashKeySerializer(new StringRedisSerializer());
    // template.afterPropertiesSet()
    template
  }
}
