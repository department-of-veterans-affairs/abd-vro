package gov.va.vro.consolegroovy

import com.fasterxml.jackson.databind.ObjectMapper
import gov.va.vro.camel.RabbitMqCamelUtils
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
  final String wireTapProducerEndpoint = RabbitMqCamelUtils.getTapProducerDirectEndpoint(tapName)

  final String wiretapId = 'origRouteWireTap'
  final String origRouteName = 'claim-submit-orig-route'
  @Override
  protected RoutesBuilder createRouteBuilder() {
    new RouteBuilder() {
          @Override
          void configure() {
            // set up original route with wireTap endpoint
            from('direct:start')
                .routeId(origRouteName)
                .wireTap(wireTapProducerEndpoint).id(wiretapId)
                .to('mock:end')
          }
        }
  }

  @BeforeEach
  void setup() {
    CamelConnection camel = new CamelConnection(context(), template)
    consoleShell = new VroConsoleShell(vroConsoleCommandsFactory, db, camel, redis)
    shell = consoleShell.setupVroShell()
  }

  @Test
  void shellRunsWithoutError() {
    def returnCode = consoleShell.startShell()
    assertEquals returnCode, 0
  }

  @Test
  void wireTapTest() {
    // subscribe to wireTap with the console route
    shell.execute("wireTap ${tapName}")

    sendMessageAndCheckAssertions()
  }

  @Test
  void wireTapVariableTest() {
    shell.execute("tapName = '${tapName}'")
    shell.execute("wireTap tapName")

    sendMessageAndCheckAssertions()
  }

  void sendMessageAndCheckAssertions(){
    AdviceWith.adviceWith(context(), origRouteName, { rb ->
      // replace the rabbitmq wiretap with seda so we don't have to set up rabbitmq in rb test
      rb.weaveById(wiretapId).replace().to("seda:wiretap")
    })
    AdviceWith.adviceWith(context(), WireTap.WireTapRoute.routeId(tapName), { rb ->
      // replace the rabbitmq wiretap with seda so we don't have to set up rabbitmq in rb test
      rb.replaceFromWith("seda:wiretap")
      // mock the output destination set up by WireTapRoute
      rb.mockEndpoints("log:"+tapName+"?*")
    })

    // send a message in the original route
    def body = '{ "body": "payload" }'
    template.requestBody('direct:start', body)

    // expect the same body in the original route
    getMockEndpoint("mock:end").expectedBodiesReceived(body)
    getMockEndpoint("mock:end").expectedMessageCount(1)

    // check the console wiretap route
    // log:${tapName} receives prettyPrinter output via route:
    // wiretap -> seda:console-${tapName} -> prettyPrinter -> log:${tapName}
    getMockEndpoint("mock:log:"+tapName).expectedMessageCount(1)
    getMockEndpoint("mock:log:"+tapName).whenAnyExchangeReceived({ ex ->
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
