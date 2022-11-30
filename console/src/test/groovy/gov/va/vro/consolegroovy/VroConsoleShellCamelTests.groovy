package gov.va.vro.consolegroovy

import com.fasterxml.jackson.databind.ObjectMapper
import gov.va.vro.consolegroovy.commands.WireTap
import org.apache.camel.RoutesBuilder
import org.apache.camel.builder.AdviceWith
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.test.junit5.CamelTestSupport
import org.apache.groovy.groovysh.Command
import org.apache.groovy.groovysh.Groovysh
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

import static org.junit.jupiter.api.Assertions.*

@ExtendWith(MockitoExtension)
class VroConsoleShellCamelTests extends CamelTestSupport {

  @Mock
  DatabaseConnection db

  @Mock
  RedisConnection redis

  Closure<List<Command>> vroConsoleCommandsFactory = new VroConsoleConfig().vroConsoleCommandsFactory(new ObjectMapper())

  VroConsoleShell consoleShell
  Groovysh shell

  final String tapName = 'claim-submitted'
  final String wireTapEndpoint = "seda:console-${tapName}"

  @Override
  protected RoutesBuilder createRouteBuilder() {
    new RouteBuilder() {
          @Override
          void configure() {
            from('direct:start')
                .routeId('claim-submit')
                .wireTap(wireTapEndpoint)
                .to('mock:end')
          }
        }
  }

  @BeforeEach
  void setup() {
    CamelConnection camel = new CamelConnection(context(), template)
    consoleShell = new VroConsoleShell(vroConsoleCommandsFactory, db, camel, redis)
    shell = consoleShell.setupVroShell()

    WireTap wireTapCommand = shell.findCommand('wireTap')
    wireTapCommand.wireTapSubscriptionEndpoint = { tapName -> wireTapEndpoint }
  }

  @Test
  void shellRunsWithoutError() {
    def returnCode = consoleShell.startShell()
    assertEquals returnCode, 0
  }

  @Test
  void wireTapTest() {
    shell.execute("wireTap ${tapName}")
    checkAssertions()
  }

  @Test
  void wireTapVariableTest() {
    shell.execute("tapName = '${tapName}'")
    shell.execute("wireTap tapName")
    checkAssertions()
  }

  void checkAssertions(){
    AdviceWith.adviceWith(context(), "console-${tapName}", { a ->
      a.mockEndpoints("log:claim-submitted?*")
    })

    def body = '{ "body": "payload" }'
    template.requestBody('direct:start', body)

    // expect the same body
    getMockEndpoint("mock:end").expectedBodiesReceived(body)
    getMockEndpoint("mock:end").expectedMessageCount(1)

    // log:${tapName} receives prettyPrinter output via route:
    // wiretap -> seda:console-${tapName} -> prettyPrinter -> log:${tapName}
    getMockEndpoint("mock:log:claim-submitted").expectedMessageCount(1)
    getMockEndpoint("mock:log:claim-submitted").whenAnyExchangeReceived({ ex ->
      def logString = ex.getIn().getBody(String)
      // The message contains the original body's content:
      assertTrue(logString.contains('"body": "payload"'))
      // but logString has been transformed by prettyPrinter:
      assertNotEquals(body, logString)

      // Compare message and body without whitespaces
      def bodyNoWhitespaces = body.replaceAll("\\s", "")
      def logStringNoWhitespaces = logString.replaceAll("\\s", "")
      assertEquals(bodyNoWhitespaces, logStringNoWhitespaces)
    })

    assertMockEndpointsSatisfied()
  }
}
