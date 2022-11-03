package gov.va.vro.service.provider.camel;

import gov.va.vro.service.spi.model.Claim;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Used by ClaimProcessorRoute to dynamically route claim to endpoints depending on claim
 * attributes. https://camel.apache.org/components/3.11.x/eips/dynamicRouter-eip.html.
 */
@Slf4j
@Component
public class SlipClaimSubmitRouter {

  private static final long DEFAULT_REQUEST_TIMEOUT = 60000;

  /**
   * Computes endpoint where claim should be routed next.
   *
   * @param claim the message body
   * @return endpoints to go, or <tt>null</tt> to indicate the end
   */
  public String routeClaimSubmit(Claim claim) {
    String diagnosticCode = claim.getDiagnosticCode();
    String route =
        String.format(
            "rabbitmq:claim-submit-exchange?queue=claim-submit&"
                + "routingKey=code.%s&requestTimeout=%d",
            diagnosticCode, DEFAULT_REQUEST_TIMEOUT);
    log.info("Routing to {}.", route);
    return route;
  }

  /**
   * Computes endpoint where health data should be routed next.
   *
   * @param claim the message body
   * @return endpoints to go, or <tt>null</tt> to indicate the end
   */
  public String routeHealthAssess(Claim claim) {
    String diagnosticCode = claim.getDiagnosticCode();
    String route =
        String.format(
            "rabbitmq:health-assess-exchange?routingKey=health-assess.%s&requestTimeout=%d",
            diagnosticCode, DEFAULT_REQUEST_TIMEOUT);
    log.info("Routing to {}.", route);
    return route;
  }
}
