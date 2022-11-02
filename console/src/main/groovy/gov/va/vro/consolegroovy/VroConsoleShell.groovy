package gov.va.vro.consolegroovy

import com.fasterxml.jackson.databind.ObjectMapper
import gov.va.vro.consolegroovy.commands.PrintJson
import gov.va.vro.consolegroovy.commands.WireTap
import gov.va.vro.persistence.repository.ClaimRepository
import gov.va.vro.persistence.repository.VeteranRepository
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
import org.springframework.stereotype.Service

@Service
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

  String submitSeda() {
    return producerTemplate.requestBody("seda:foo", "Hello", String)
  }

  @EventListener(ApplicationReadyEvent)
  void startShell() {
    def userDir = System.getProperty("user.dir")
    println("Working Directory = " + userDir)

    def shell = createGroovysh(getBinding())
    shell.register(new PrintJson(shell, objectMapper))
    shell.register(new WireTap(shell, camelContext))

    // Don't limit the log message length since WireTap prints out the message body
    // https://camel.apache.org/manual/faq/how-do-i-set-the-max-chars-when-debug-logging-messages-in-camel.html
    camelContext.globalOptions.put(Exchange.LOG_DEBUG_BODY_MAX_CHARS, "0")

    shell.run("")
    println 'Exiting'
  }

  def getBinding() {
    new Binding([
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
