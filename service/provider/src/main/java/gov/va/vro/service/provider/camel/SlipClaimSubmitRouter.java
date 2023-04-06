package gov.va.vro.service.provider.camel;

import gov.va.vro.service.provider.services.DiagnosisLookup;
import gov.va.vro.service.spi.model.Claim;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ExchangeProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Used by ClaimProcessorRoute to dynamically route claim to endpoints depending on claim
 * attributes. https://camel.apache.org/components/3.11.x/eips/dynamicRouter-eip.html.
 */
@Slf4j
@Component
public class SlipClaimSubmitRouter {

  private static final long DEFAULT_REQUEST_TIMEOUT = 120000;
  public static final String NO_DIAGNOSTIC_CODE_ERROR = "No diagnostic code in properties.";

  /**
   * Computes endpoint where claim should be routed next.
   *
   * @param claim the message body
   * @return endpoints to go, or <tt>null</tt> to indicate the end
   */
  public String routeClaimSubmit(Claim claim) {
    String diagnosis = DiagnosisLookup.getDiagnosis(claim.getDiagnosticCode()).toLowerCase();
    String route =
        String.format(
            "rabbitmq:claim-submit-exchange?queue=claim-submit&"
                + "routingKey=code.%s&requestTimeout=%d",
            diagnosis, DEFAULT_REQUEST_TIMEOUT);
    log.info(
        "Routing to {} for claim {} in collection {}",
        route,
        claim.getBenefitClaimId(),
        claim.getCollectionId());
    return route;
  }

  /**
   * Computes endpoint where health data should be routed next.
   *
   * @param body the message body
   * @param props the exchange properties where we can store state between invocations
   * @return endpoints to go, or <tt>null</tt> to indicate the end
   */
  @SneakyThrows
  public String routeHealthAssess(Object body, @ExchangeProperties Map<String, Object> props) {
    String diagnosticCode = getDiagnosticCode(props);
    String diagnosis = DiagnosisLookup.getDiagnosis(diagnosticCode).toLowerCase();
    String route =
        String.format(
            "rabbitmq:health-assess-exchange?routingKey=health-assess.%s&requestTimeout=%d",
            diagnosis, DEFAULT_REQUEST_TIMEOUT);
    log.info("Routing to {}.", route);
    return route;
  }

  /**
   * Route health sufficiency.
   *
   * @param body body object
   * @param props exchange properties map
   * @return string
   */
  @SneakyThrows
  public String routeHealthSufficiency(Object body, @ExchangeProperties Map<String, Object> props) {
    String diagnosticCode = getDiagnosticCode(props);
    String diagnosis = DiagnosisLookup.getDiagnosis(diagnosticCode).toLowerCase();
    String route =
        String.format(
            "rabbitmq:health-assess-exchange?routingKey=health"
                + "-sufficiency-assess.%s&requestTimeout=%d",
            diagnosis, DEFAULT_REQUEST_TIMEOUT);
    log.info("Routing to {}.", route);
    return route;
  }

  private static String getDiagnosticCode(Map<String, Object> props) {
    Object diagnosticCodeObj = props.get("diagnosticCode");
    if (diagnosticCodeObj == null) {
      log.error(NO_DIAGNOSTIC_CODE_ERROR);
      throw new CamelProcessingException(NO_DIAGNOSTIC_CODE_ERROR);
    }
    return diagnosticCodeObj.toString();
  }
}
