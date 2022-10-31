package gov.va.vro.consolegroovy.commands

import groovy.transform.CompileStatic
import org.apache.camel.CamelContext
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

    this.camelContext=camelContext
  }

  Object execute(final List<String> args) {
    log.debug "args: ${args}"
    args.collect({ String it ->
      Object varObj = variables.get(it)
      // in case `it` is executable
      if (varObj == null) varObj = shell.execute(it)

      if (varObj == null) return "null"

      subscribeToTopic(it)
    }).join('\n')
  }

  void subscribeToTopic(String wireTapName){
    RoutesBuilder routeBuilder=new WireTapRoute(wireTapName)
    camelContext.addRoutes(routeBuilder)
  }

  class WireTapRoute extends RouteBuilder {
    final String tapName;
    WireTapRoute(String wireTapName){
      this.tapName=wireTapName
    }

    @Override
    void configure() throws Exception {
      from("rabbitmq:tap-${tapName}?exchangeType=topic&queue=console-${tapName}")
          .routeId("console-${tapName}").to("log:${tapName}")
    }
  }
}
