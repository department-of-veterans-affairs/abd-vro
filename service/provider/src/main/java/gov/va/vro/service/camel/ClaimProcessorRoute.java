package gov.va.vro.service.camel;

import gov.va.starter.example.service.spi.claimsubmission.model.ClaimSubmission;
import gov.va.vro.model.Payload;
import gov.va.vro.service.processors.ClaimProcessorA;
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
        .setProperty("contentionType", simple("${body.contentionType}"))
        //                .tracing()
        .dynamicRouter(method(ClaimProcessorRoute.class, "route"));

    from("seda:claimTypeA")
        .routeId("seda-claimTypeA")
        //                .tracing()
        .log(">>> ${body}")
        .delayer(5000)
        .bean(ClaimProcessorA.class, "process")
        .end();
  }

  private static final String SEDA_ASYNC_OPTION = "?waitForTaskToComplete=Never";

  /**
   * Use this method to compute dynamic where we should route next.
   * https://camel.apache.org/components/3.11.x/eips/dynamicRouter-eip.html
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
      String claimType = (String) props.get("contentionType");
      if (claimType == null) {
        System.err.println("ERROR: null contentionType");
        return null;
      }
      log.info("+++ invoked=1 " + claimType + " " + body.getClass() + " " + props);
      switch (claimType) {
        case "A":
          return "seda:claimType" + claimType; // non-async endpoint; wait for result
        case "B": // Groovy
        case "C": // Ruby in separate process
        default:
          System.err.println("ERROR: unknown contentionType: " + claimType);
          return null;
      }
    } else if (invoked == 2) {
      String submissionId;
      if (body instanceof Payload) submissionId = ((Payload) body).getSubmissionId();
      else if (body instanceof byte[])
        submissionId =
            new CamelDtoConverter(null).toPojo(Payload.class, (byte[]) body).getSubmissionId();
      else if (body instanceof ClaimSubmission)
        submissionId = ((ClaimSubmission) body).getSubmissionId();
      else throw new IllegalArgumentException("body " + body.getClass());
      log.info("+++ invoked=2 " + submissionId + " " + body.getClass() + " " + props);
      String generalSeda = "seda:claim-vro-processed";
      String specificSeda = "seda:claim-vro-processed-" + submissionId + SEDA_ASYNC_OPTION;
      return specificSeda + "," + generalSeda;
    }

    log.info("+++ invoked=" + invoked + " " + body.getClass() + " " + props);
    // no more so return null
    return null;
  }
}
