package gov.va.vro.service.provider.camel;

import gov.va.vro.camel.FunctionProcessor;
import gov.va.vro.service.provider.bip.service.BipClaimService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/** @author warren @Date 11/8/22 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BipIntegrationRoutes extends RouteBuilder {

  public static final String ENDPOINT_MAS_OFFRAMP = "direct:mas-offramp";

  private final BipClaimService bipClaimService;

  @Override
  public void configure() {
    configureOffRampClaim();
  }

  private void configureOffRampClaim() {
    // TODO: complete route
    from(ENDPOINT_MAS_OFFRAMP)
        .routeId("mas-offramp-claim")
        .log("Request to off-ramp claim received")
        .bean(FunctionProcessor.fromFunction(bipClaimService::removeSpecialIssue))
        .choice()
        .when(simple("${exchangeProperty.sufficientForFastTracking}"))
        .bean(FunctionProcessor.fromFunction(bipClaimService::markAsRFD))
        .end()
        .bean(bipClaimService, "completeProcessing");
  }
}
