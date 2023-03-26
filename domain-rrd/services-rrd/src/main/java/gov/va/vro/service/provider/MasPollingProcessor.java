package gov.va.vro.service.provider;

import gov.va.vro.model.rrd.mas.MasAutomatedClaimPayload;
import gov.va.vro.service.provider.camel.MasIntegrationRoutes;
import gov.va.vro.service.provider.mas.MasException;
import gov.va.vro.service.provider.mas.service.MasCollectionService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MasPollingProcessor implements Processor {
  private static final String MAS_RETRY_LIMIT_PASSED =
      "MAS Processing did not complete. Maximum reties exceeded.";
  private final CamelEntrance camelEntrance;
  private final MasConfig masDelays;
  private final MasCollectionService masCollectionService;

  @Override
  @SneakyThrows
  public void process(Exchange exchange) {
    int retryCounts = (int) exchange.getMessage().getHeader(MasIntegrationRoutes.MAS_RETRY_PARAM);
    var claimPayload = exchange.getMessage().getBody(MasAutomatedClaimPayload.class);
    if (retryCounts == 0) {
      String msg =
          String.format(
              "%s Collection ID: %s, Claim ID: %s",
              MAS_RETRY_LIMIT_PASSED,
              claimPayload.getCollectionId(),
              claimPayload.getBenefitClaimId());
      throw new MasException(msg);
    }

    boolean isCollectionReady =
        masCollectionService.checkCollectionStatus(claimPayload.getCollectionId());
    log.info(
        "Collection status is {} for collection ID: {}, claim ID: {}, icn: {}",
        isCollectionReady,
        claimPayload.getCollectionId(),
        claimPayload.getBenefitClaimId(),
        claimPayload.getVeteranIcn());

    if (isCollectionReady) {
      camelEntrance.processClaim(claimPayload);
    } else {
      log.info(
          "Collection {} is not ready. Retrying... {}",
          claimPayload.getCollectionId(),
          retryCounts);
      // re-request after some time
      camelEntrance.notifyAutomatedClaim(
          claimPayload, masDelays.getMasProcessingSubsequentDelay(), retryCounts - 1);
    }
  }
}
