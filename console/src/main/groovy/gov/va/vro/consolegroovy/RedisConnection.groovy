package gov.va.vro.consolegroovy

import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
@groovy.transform.TupleConstructor(excludes = ['redisClient'])
class RedisConnection {
  @Autowired
  final LettuceConnectionFactory lettuceConnectionFactory

  @Autowired
  final RedisTemplate<String, Object> redisTemplate

  RedisClient redisClient

  RedisCommands<String, String> getRedisCommands(){
    redisClient = redisClient ?: lettuceConnectionFactory.getNativeClient()
    StatefulRedisConnection<String, String> connection = redisClient.connect()
    connection.sync()
  }
}
