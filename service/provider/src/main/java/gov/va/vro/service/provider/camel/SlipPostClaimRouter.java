package gov.va.vro.service.provider.camel;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ExchangeProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * Used by ClaimProcessorRoute to dynamically route claim to endpoints depending on claim
 * attributes. https://camel.apache.org/components/3.11.x/eips/dynamicRouter-eip.html
 */
@Slf4j
@Component
public class DynamicPostClaimRouter {
  /**
   * Computes endpoint where claim should be routed next.
   *
   * @param body the message body
   * @param props the exchange properties where we can store state between invocations
   * @return endpoints to go, or <tt>null</tt> to indicate the end
   */
  public String routePostClaim(Object body, @ExchangeProperties Map<String, Object> props)
      throws IOException {
    Object diagnosticCodeObj = props.get("diagnosticCode");
    if (diagnosticCodeObj == null) {
      log.error("No diagnotic code in the body.");
      return null;
    }
    int diagnosticCode = Integer.parseInt(diagnosticCodeObj.toString());
    String route =
        "rabbitmq:claim-submit-exchange"
            + "?queue=claim-submit"
            + "&routingKey=code."
            + diagnosticCode;

    log.info("Routing to {}.", route);
    return route;
  }
}
