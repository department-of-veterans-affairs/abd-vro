package gov.va.vro.consolegroovy.commands

import groovy.json.JsonOutput
import groovy.transform.CompileStatic
import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.RoutesBuilder
import org.apache.camel.builder.RouteBuilder
import org.apache.groovy.groovysh.CommandSupport
import org.apache.groovy.groovysh.Groovysh

@CompileStatic
class WireTap extends CommandSupport {
  CamelContext camelContext

  protected WireTap(final Groovysh shell, final CamelContext camelContext) {
    super(shell, 'wireTap', 'wt')
    // TODO: add Command description, usage, etc.

    this.camelContext = camelContext
  }

  Object execute(final List<String> args) {
    log.debug "args: ${args}"
    args.collect({ String it ->
      String varObj = variables.get(it)
      if (varObj == null)
        subscribeToTopic(it)
      else
        subscribeToTopic(varObj)
    })
  }

  Closure<String> wireTapSubscriptionEndpoint = { String tapName ->
    "rabbitmq:tap-${tapName}?exchangeType=topic&queue=console-${tapName}".toString()
  }

  String subscribeToTopic(String wireTapName) {
    String tapEndpoint = wireTapSubscriptionEndpoint(wireTapName)
    RoutesBuilder routeBuilder = new WireTapRoute(wireTapName, tapEndpoint)
    camelContext.addRoutes(routeBuilder)
    "tap-${wireTapName}"
  }

  @groovy.transform.TupleConstructor
  static class WireTapRoute extends RouteBuilder {
    final String tapName
    final String tapEndpoint

    @Override
    void configure() throws Exception {
      from(tapEndpoint)
          .routeId("console-${tapName}")
          .process(prettyPrinter)
          .to("log:${tapName}?plain=true")
    }

    Processor prettyPrinter = { Exchange exchange ->
      def json = exchange.getIn().getBody(String)
      exchange.getMessage().setBody(JsonOutput.prettyPrint(json))
    }
  }
}
