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
    log.warn "args: ${args}"
    args.collect({ String it ->
      String varObj = variables.get(it)
      if (varObj == null)
        subscribeToTopic(it)
      else
        subscribeToTopic(varObj)
    })
  }

  String subscribeToTopic(String wireTapName) {
    RoutesBuilder routeBuilder = new WireTapRoute(wireTapName)
    camelContext.addRoutes(routeBuilder)
    "tap-${wireTapName}"
  }

  static class WireTapRoute extends RouteBuilder {
    final String tapName;

    WireTapRoute(String wireTapName) {
      this.tapName = wireTapName
    }

    @Override
    void configure() throws Exception {
      from("rabbitmq:tap-${tapName}?exchangeType=topic&queue=console-${tapName}")
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
