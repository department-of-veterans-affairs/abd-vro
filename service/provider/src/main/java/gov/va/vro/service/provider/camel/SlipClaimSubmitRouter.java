package gov.va.vro.service.provider.camel;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ExchangeProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Used by ClaimProcessorRoute to dynamically route claim to endpoints depending on claim
 * attributes. https://camel.apache.org/components/3.11.x/eips/dynamicRouter-eip.html
 */
@Slf4j
@Component
public class SlipClaimSubmitRouter {

  private static final long DEFAULT_REQUEST_TIMEOUT = 60000;

  private static final String RABBIT_ROUTE_TEMPLATE =
      "rabbitmq:claim-submit-exchange?queue=%s&routingKey=code.%s&requestTimeout=%d";

  /**
   * Computes endpoint where claim should be routed next.
   *
   * @param body the message body
   * @param props the exchange properties where we can store state between invocations
   * @return endpoints to go, or <tt>null</tt> to indicate the end
   */
  public String routeClaimSubmit(Object body, @ExchangeProperties Map<String, Object> props) {
    Object diagnosticCodeObj = props.get("diagnosticCode");
    if (diagnosticCodeObj == null) {
      log.error("No diagnostic code in the body.");
      return null;
    }
    String diagnosticCode = diagnosticCodeObj.toString();
    String route =
        String.format(
            RABBIT_ROUTE_TEMPLATE, "claim-submit", diagnosticCode, DEFAULT_REQUEST_TIMEOUT);
    log.info("Routing to {}.", route);
    return route;
  }

  /**
   * Computes endpoint where health data should be routed next.
   *
   * @param body the message body
   * @param props the exchange properties where we can store state between invocations
   * @return endpoints to go, or <tt>null</tt> to indicate the end
   */
  public String routeClaimSubmitFull(Object body, @ExchangeProperties Map<String, Object> props) {
    Object diagnosticCodeObj = props.get("diagnosticCode");
    if (diagnosticCodeObj == null) {
      log.error("No diagnostic code in the body.");
      return null;
    }
    String diagnosticCode = diagnosticCodeObj.toString();
    String route =
        String.format(
            RABBIT_ROUTE_TEMPLATE,
            "health-assess-exchange",
            diagnosticCode,
            DEFAULT_REQUEST_TIMEOUT);
    log.info("Routing to {}.", route);
    return route;
  }
}
