package gov.va.vro.consolegroovy

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.fppt.jedismock.RedisServer
import io.lettuce.core.RedisClient
import org.apache.groovy.groovysh.Command
import org.apache.groovy.groovysh.Groovysh
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

@ExtendWith(MockitoExtension)
class VroConsoleShellRedisTests {

  @Mock
  DatabaseConnection db

  @Mock
  CamelConnection camel

  @Mock
  RedisConnection redis

  Closure<List<Command>> vroConsoleCommandsFactory = new VroConsoleConfig().vroConsoleCommandsFactory(new ObjectMapper())

  VroConsoleShell consoleShell
  Groovysh shell

  @BeforeEach
  void setup() {
    // This binds mock redis server to a random port
    RedisServer server = RedisServer.newRedisServer().start()

    RedisConnection redis = new RedisConnection()
    redis.redisClient = RedisClient.create(String.format("redis://%s:%s", server.getHost(), server.getBindPort()));

    consoleShell = new VroConsoleShell(vroConsoleCommandsFactory, db, camel, redis)
    shell = consoleShell.setupVroShell()
  }

  @Test
  void shellRunsWithoutError() {
    def returnCode = consoleShell.startShell()
    assertEquals returnCode, 0
  }

  @Test
  void readRedisTest() {
    shell.execute('redis.keys "*"')
    shell.execute("redis.hset 'someRedisKey', 'hashKey1', '1234'")
    String keysOutput = shell.execute('redis.keys "*"')
    assertTrue keysOutput.contains('someRedisKey')

    String hkeysOutput = shell.execute('redis.hkeys "someRedisKey"')
    assertTrue hkeysOutput.contains('[hashKey1]')

    String hgetOutput = shell.execute('redis.hget "someRedisKey", "hashKey1"')
    assertTrue hgetOutput.contains('1234')
  }
}
