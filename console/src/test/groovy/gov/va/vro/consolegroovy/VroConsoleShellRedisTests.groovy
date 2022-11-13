package gov.va.vro.consolegroovy

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.fppt.jedismock.RedisServer
import gov.va.vro.persistence.repository.ClaimRepository
import gov.va.vro.persistence.repository.VeteranRepository
import io.lettuce.core.RedisClient
import org.apache.camel.CamelContext
import org.apache.camel.ProducerTemplate
import org.apache.groovy.groovysh.Groovysh
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue
import static org.mockito.Mockito.doReturn

@ExtendWith(MockitoExtension)
class VroConsoleShellRedisTests {
  @Mock
  CamelContext camelContext

  ObjectMapper objectMapper = new ObjectMapper()

  @Mock
  ProducerTemplate producerTemplate

  @Mock
  ClaimRepository claimRepository

  @Mock
  VeteranRepository veteranRepository

  @Mock
  LettuceConnectionFactory lettuceConnectionFactory

  VroConsoleShell consoleShell
  Groovysh shell

  @BeforeEach
  void setup() {
    // This binds mock redis server to a random port
    doReturn(new HashMap()).when(camelContext).getGlobalOptions()
    consoleShell = new VroConsoleShell(camelContext, objectMapper, producerTemplate, claimRepository, veteranRepository, lettuceConnectionFactory)

    RedisServer server = RedisServer.newRedisServer().start()
    consoleShell.redisClient = RedisClient.create(String.format("redis://%s:%s", server.getHost(), server.getBindPort()));
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
