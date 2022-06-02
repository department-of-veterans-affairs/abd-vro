package gov.va.vro.service.provider.camel;

import gov.va.vro.service.provider.processors.ClaimProcessorA;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/** Routes claims to processors, depending on claim attributes (e.g., contentionType) */
@Component
@RequiredArgsConstructor
public class ClaimProcessorRoute extends RouteBuilder {
  private final CamelUtils camelUtils;

  @Override
  public void configure() throws Exception {
    configureRouteClaimRouter();
    configureRouteClaimTypeA();
  }

  private void configureRouteClaimRouter() {
    camelUtils.asyncSedaEndpoint("seda:claim-router");
    from("seda:claim-router")
        .routeId("routing-claim")
        // Use Properties not Headers
        // https://examples.javacodegeeks.com/apache-camel-headers-vs-properties-example/
        .setProperty("contentionType", simple("${body.contentionType}"))
        // .tracing()
        .dynamicRouter(method(DynamicClaimRouter.class, "routeClaim"));
  }

  private void configureRouteClaimTypeA() {
    from("seda:claimTypeA")
        .routeId("seda-claimTypeA")
        .log(">>4> ${body}")
        .delayer(5000) // artificial delay to simulate processing
        .bean(ClaimProcessorA.class, "process")
        .end();
  }
}
