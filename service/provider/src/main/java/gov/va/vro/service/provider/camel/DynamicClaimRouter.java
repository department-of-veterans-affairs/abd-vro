package gov.va.vro.service.provider.camel;

import gov.va.starter.example.service.spi.claimsubmission.model.ClaimSubmission;
import gov.va.vro.persistence.model.PayloadEntity;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class DynamicClaimRouter {

  private static final String SEDA_ASYNC_OPTION = "?waitForTaskToComplete=Never";

  private final CamelDtoConverter dtoConverter;

  /**
   * Computes endpoint where claim should be routed next.
   *
   * @param body the message body
   * @param props the exchange properties where we can store state between invocations
   * @return endpoints to go, or <tt>null</tt> to indicate the end
   */
  public String routeClaim(Object body, @ExchangeProperties Map<String, Object> props)
      throws IOException {
    // get the state from the exchange props and keep track how many times
    // we have been invoked
    int invoked = 0;
    Object current = props.get("invoked");
    if (current != null) {
      invoked = Integer.parseInt(current.toString());
    }
    invoked++;
    // and store the state back on the props
    props.put("invoked", invoked);

    if (invoked == 1) {
      String claimType = (String) props.get("contentionType");
      if (claimType == null) {
        log.error("null contentionType");
        return null;
      }
      log.debug("+++ invoked=1 " + claimType + " " + body.getClass() + " " + props);
      switch (claimType) {
        case "A":
          return "seda:claimType" + claimType; // non-async endpoint; wait for result
        case "C": // Ruby in separate process
        default:
          log.error("unknown contentionType: {}", claimType);
          return null;
      }
    } else if (invoked == 2) {
      String submissionId;
      if (body instanceof PayloadEntity) submissionId = ((PayloadEntity) body).getSubmissionId();
      else if (body instanceof byte[])
        submissionId = dtoConverter.toPojo(PayloadEntity.class, (byte[]) body).getSubmissionId();
      else if (body instanceof ClaimSubmission)
        submissionId = ((ClaimSubmission) body).getSubmissionId();
      else throw new IllegalArgumentException("body " + body.getClass());
      String specificSeda = "seda:claim-vro-processed-" + submissionId + SEDA_ASYNC_OPTION;
      log.debug("+++ invoked=2 " + submissionId + " " + body.getClass() + " " + props);

      // send to general and specific queues
      String generalSeda = "seda:claim-vro-processed";
      return specificSeda + "," + generalSeda;
    }

    log.debug("+++ invoked=" + invoked + " " + body.getClass() + " " + props);
    // end dynamic routing
    return null;
  }
}
