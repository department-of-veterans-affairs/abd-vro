package gov.va.vro.consolegroovy

import com.fasterxml.jackson.databind.ObjectMapper
import gov.va.vro.consolegroovy.commands.PrintJson
import gov.va.vro.consolegroovy.commands.WireTap
import gov.va.vro.persistence.repository.ClaimRepository
import gov.va.vro.persistence.repository.VeteranRepository
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.ProducerTemplate
import org.apache.groovy.groovysh.Groovysh
import org.apache.groovy.groovysh.util.PackageHelper
import org.codehaus.groovy.tools.shell.IO
import org.codehaus.groovy.tools.shell.util.Preferences
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
@groovy.transform.TupleConstructor
class VroConsoleShell {
  @Autowired
  CamelContext camelContext

  @Autowired
  ObjectMapper objectMapper

  @Autowired
  ProducerTemplate producerTemplate

  @Autowired
  ClaimRepository claimRepository

  @Autowired
  VeteranRepository veteranRepository

  @Autowired
  LettuceConnectionFactory lettuceConnectionFactory

  RedisClient redisClient

  RedisCommands<String, String> redisConnection(){
    redisClient ?= lettuceConnectionFactory.getNativeClient()
    StatefulRedisConnection<String, String> connection = redisClient.connect()
    connection.sync()
  }

  @Autowired
  RedisTemplate<String, Object> redisTemplate

  @EventListener(ApplicationReadyEvent)
  int startShell() {
    def userDir = System.getProperty("user.dir")
    println("Working Directory = " + userDir)

    def shell = setupVroShell()

    def returnCode = shell.run("")
    println 'Exiting'
    returnCode
  }

  Groovysh setupVroShell(){
    def shell = createGroovysh(getBinding())
    shell.register(new PrintJson(shell, objectMapper))
    shell.register(new WireTap(shell, camelContext))

    // Don't limit the log message length since WireTap prints out the message body
    // https://camel.apache.org/manual/faq/how-do-i-set-the-max-chars-when-debug-logging-messages-in-camel.html
    camelContext.getGlobalOptions().put(Exchange.LOG_DEBUG_BODY_MAX_CHARS, "0")

    shell
  }

  def getBinding() {
    new Binding([
      redis: redisConnection(),
      redisT: redisTemplate,
      claimsT: claimRepository,
      vetT   : veteranRepository,
      camel  : camelContext,
      pt     : producerTemplate
    ])
  }

  def createGroovysh(Binding binding = null) {
    IO io = new IO(System.in, System.out, System.err)
    // io.setVerbosity(IO.Verbosity.DEBUG)

    // workaround so that `java -jar ...` works
    Preferences.put(PackageHelper.IMPORT_COMPLETION_PREFERENCE_KEY, "true")

    new Groovysh(binding, io)
  }
}
