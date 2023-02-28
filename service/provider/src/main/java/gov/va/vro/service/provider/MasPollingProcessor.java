package gov.va.vro.service.provider;

import gov.va.vro.model.mas.MasAutomatedClaimPayload;
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
  private final CamelEntrance camelEntrance;
  private final MasConfig masDelays;
  private final MasCollectionService masCollectionService;

  @Override
  @SneakyThrows
  public void process(Exchange exchange) {
    int retryCounts = (int) exchange.getMessage().getHeader(MasIntegrationRoutes.MAS_RETRY_PARAM);
    if (retryCounts == 0) {
      throw new MasException("MAS Processing did not complete. Maximum reties exceeded");
    }

    var claimPayload = exchange.getMessage().getBody(MasAutomatedClaimPayload.class);

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
      log.info("Collection {} is not ready. Requeue..ing...", claimPayload.getCollectionId());
      // re-request after some time
      camelEntrance.notifyAutomatedClaim(
          claimPayload, masDelays.getMasProcessingSubsequentDelay(), retryCounts - 1);
    }
  }
}
