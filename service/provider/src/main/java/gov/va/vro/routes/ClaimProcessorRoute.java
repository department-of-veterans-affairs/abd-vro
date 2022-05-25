package gov.va.vro.routes;

import gov.va.starter.example.persistence.model.ClaimSubmissionEntity;
import gov.va.vro.DtoConverter;
import gov.va.vro.model.Payload;
import gov.va.vro.services.ClaimProcessorA;
import org.apache.camel.ExchangeProperties;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class ClaimProcessorRoute extends RouteBuilder {
  @Autowired private CamelUtils camelUtils;

  @Override
  public void configure() throws Exception {
    from("seda:claim-router")
        .routeId("routing-claim")
        // Use Properties not Headers
        // https://examples.javacodegeeks.com/apache-camel-headers-vs-properties-example/
        .setProperty("contention_type", simple("${body.contention_type}"))
        //                .tracing()
        .dynamicRouter(method(ClaimProcessorRoute.class, "route"));

    from("seda:claimTypeA")
        .routeId("seda-claimTypeA")
        //                .tracing()
        .log(">>> ${body}")
        .delayer(5000)
        .bean(ClaimProcessorA.class, "process")
        .end();

    //    from("rabbitmq:claimTypeD")
    //        .routeId("claimTypeD")
    //        //                .tracing()
    //        .log(">>> ${body}")
    //        .delayer(5000)
    //        .bean(ClaimProcessorD.class, "process")
    //        .end();
  }

  private static final String SEDA_ASYNC_OPTION = "?waitForTaskToComplete=Never";

  /**
   * Use this method to compute dynamic where we should route next.
   *
   * @param body the message body
   * @param props the exchange properties where we can store state between invocations
   * @return endpoints to go, or <tt>null</tt> to indicate the end
   */
  public String route(Object body, @ExchangeProperties Map<String, Object> props)
      throws IOException {
    //        bodies.add(claim);

    // get the state from the exchange props and keep track how many times
    // we have been invoked
    int invoked = 0;
    Object current = props.get("invoked");
    if (current != null) {
      invoked = Integer.parseInt(current.toString());
    }
    invoked++;
    //        System.err.println("props: " + props);
    // and store the state back on the props
    props.put("invoked", invoked);

    if (invoked == 1) {
      String claimType = (String) props.get("contention_type");
      switch (claimType) {
        case "A":
          return "seda:claimType" + claimType; // wait for result // + SEDA_ASYNC_OPTION;
        case "B": // Groovy
        case "C": // Ruby in separate process
        case "D": // JRuby
          System.err.println("sending to rabbitmq:claimType" + claimType);
          return "rabbitmq:claimType" + claimType;
        default:
          System.err.println("ERROR: unknown contention_type: " + claimType);
          return null;
      }
    } else if (invoked == 2) {
      String submissionId;
      if (body instanceof Payload) submissionId = ((Payload) body).getSubmissionId();
      else if (body instanceof byte[])
        submissionId = DtoConverter.toPojo(Payload.class, (byte[]) body).getSubmissionId();
      else if (body instanceof ClaimSubmissionEntity)
        submissionId = ((ClaimSubmissionEntity) body).getSubmissionId();
      else throw new IllegalArgumentException("body " + body.getClass());
      return "seda:claim-vro-processed-" + submissionId + SEDA_ASYNC_OPTION;
    }

    // no more so return null
    return null;
  }
}
