package gov.va.vro.consolegroovy

import org.apache.camel.builder.RouteBuilder
import org.springframework.stereotype.Component

@Component
class ConsoleRoutes extends RouteBuilder {

  @Override
  void configure() throws Exception {
    from("seda:foo").to("log:bar")
  }
}
