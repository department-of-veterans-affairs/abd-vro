package gov.va.vro.consolegroovy

import com.fasterxml.jackson.databind.ObjectMapper
import gov.va.vro.consolegroovy.commands.PrintJson
import gov.va.vro.consolegroovy.commands.WireTap
import org.apache.groovy.groovysh.Command
import org.apache.groovy.groovysh.Groovysh
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
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
  @Bean
  RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> template = new RedisTemplate<>()
    template.setConnectionFactory(lettuceConnectionFactory())
    template.setDefaultSerializer(new StringRedisSerializer())
    template
  }

  @Autowired
  final ObjectMapper objectMapper

  @Bean
  Closure<List<Command>> vroConsoleCommandsFactory(ObjectMapper objectMapper){
    return { Groovysh shell, VroConsoleShell vroShell ->
      List.of(
          new PrintJson(shell, objectMapper),
          new WireTap(shell, vroShell.camel.camelContext)
          )
    }
  }
}
